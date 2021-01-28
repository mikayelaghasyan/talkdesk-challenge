package talkdesk.challenge.core.runtime;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import talkdesk.challenge.core.communication.CommunicationBus;
import talkdesk.challenge.core.db.DbGateway;
import talkdesk.challenge.core.domainevent.DomainEventBus;
import talkdesk.challenge.core.serdes.DurationDeserializer;
import talkdesk.challenge.core.serdes.DurationSerializer;

import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class Application implements ApplicationContext {
  private static final Logger log = LoggerFactory.getLogger(Application.class);

  private final Vertx vertx;

  private JsonObject config;

  private CommunicationBus communicationBus;
  private DomainEventBus eventBus;
  private DbGateway dbGateway;

  public Application() {
    this(Vertx.vertx());
  }

  public Application(Vertx vertx) {
    this.vertx = vertx;
  }

  public Future<Void> run(String[] args) {
    ConfigRetrieverOptions options = new ConfigRetrieverOptions();
    Optional.ofNullable(defaultConfigStore())
      .map(options::addStore);
    Arrays.stream(args).map(arg -> new ConfigStoreOptions()
      .setType("file")
      .setOptional(true)
      .setConfig(new JsonObject().put("path", arg)))
      .forEach(options::addStore);
    options.addStore(new ConfigStoreOptions()
      .setType("env")
      .setConfig(new JsonObject().put("raw-data", true)));
    return ConfigRetriever.create(vertx, options).getConfig()
      .onSuccess(config -> {
        log.debug("Config loaded: {}", config);
        this.config = config;
        configureCodec();
        eventBus = createEventBus();
        communicationBus = createCommunicationBus();
        dbGateway = createDbGateway();
      }).map(x -> null);
  }

  private ConfigStoreOptions defaultConfigStore() {
    URL configUrl = getClass().getClassLoader()
      .getResource("conf/config.json");
    return Optional.ofNullable(configUrl)
      .map(URL::getPath)
      .map(path -> new ConfigStoreOptions()
        .setType("file")
        .setOptional(true)
        .setConfig(new JsonObject().put("path", path)))
      .orElse(null);
  }

  @Override
  public Vertx vertx() {
    return this.vertx;
  }

  @Override
  public CommunicationBus communicationBus() {
    return this.communicationBus;
  }

  @Override
  public DomainEventBus eventBus() {
    return this.eventBus;
  }

  @Override
  public DbGateway dbGateway() {
    return dbGateway;
  }

  private void configureCodec() {
    Stream.of(DatabindCodec.mapper(), DatabindCodec.prettyMapper())
      .forEach(mapper -> {
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        SimpleModule module = new SimpleModule();
        module.addSerializer(Duration.class, new DurationSerializer());
        module.addDeserializer(Duration.class, new DurationDeserializer());
        mapper.registerModule(module);
      });
  }

  private DomainEventBus createEventBus() {
    DomainEventBusFactory eventBusFactory = new DomainEventBusFactory(this);
    return Optional.ofNullable(config.getJsonObject("eventBus"))
      .flatMap(eventBusFactory::createEventBus)
      .orElseGet(eventBusFactory::createDefaultEventBus);
  }

  private CommunicationBus createCommunicationBus() {
    CommunicationBusFactory communicationBusFactory = new CommunicationBusFactory(this);
    return Optional.ofNullable(config.getJsonObject("communicationBus"))
      .flatMap(communicationBusFactory::createCommunicationBus)
      .orElseGet(communicationBusFactory::createDefaultCommunicationBus);
  }

  private DbGateway createDbGateway() {
    DbGatewayFactory dbGatewayFactory = new DbGatewayFactory(this);
    return Optional.ofNullable(config.getJsonObject("dbGateway"))
      .flatMap(dbGatewayFactory::createDbGateway)
      .orElseGet(dbGatewayFactory::createDefaultDbGateway);
  }

  public <U extends Node> void deployNode(U node) {
    vertx.deployVerticle(node)
      .compose(x -> node.run(createRuntimeContext()));
  }

  private RuntimeContext createRuntimeContext() {
    RuntimeContext context = new RuntimeContext();
    context.communicationBus(communicationBus);
    context.eventBus(eventBus);
    context.config(config);
    return context;
  }
}

package talkdesk.challenge.core.runtime;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import talkdesk.challenge.core.communication.CommunicationBus;
import talkdesk.challenge.core.db.DbGateway;
import talkdesk.challenge.core.domainevent.DomainEventBus;

import java.util.Optional;
import java.util.stream.Stream;

public class Application implements ApplicationContext {
  private final Vertx vertx;
  private final JsonObject config;

  private CommunicationBus communicationBus;
  private DomainEventBus eventBus;
  private DbGateway dbGateway;

  public Application() {
    this(Vertx.vertx());
  }

  public Application(Vertx vertx) {
    this.vertx = vertx;
    this.config = ConfigRetriever.create(vertx).getConfig().result();

    configureCodec();

    eventBus = createEventBus();
    communicationBus = createCommunicationBus();
    dbGateway = createDbGateway();
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
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.registerModule(new JavaTimeModule());
      });
  }

  private DomainEventBus createEventBus() {
    DomainEventBusFactory eventBusFactory = new DomainEventBusFactory(this);
    return Optional.ofNullable(config.getJsonObject("eventBus"))
      .map(eventBusConfig -> eventBusFactory.createEventBus(eventBusConfig))
      .orElseGet(() -> eventBusFactory.createDefaultEventBus());
  }

  private CommunicationBus createCommunicationBus() {
    CommunicationBusFactory communicationBusFactory = new CommunicationBusFactory(this);
    return Optional.ofNullable(config.getJsonObject("communicationBus"))
      .map(communicationBusConfig -> communicationBusFactory.createCommunicationBus(communicationBusConfig))
      .orElseGet(() -> communicationBusFactory.createDefaultCommunicationBus());
  }

  private DbGateway createDbGateway() {
    DbGatewayFactory dbGatewayFactory = new DbGatewayFactory(this);
    return Optional.ofNullable(config.getJsonObject("dbGateway"))
      .map(dbGatewayConfig -> dbGatewayFactory.createDbGateway(dbGatewayConfig))
      .orElseGet(() -> dbGatewayFactory.createDefaultDbGateway());
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

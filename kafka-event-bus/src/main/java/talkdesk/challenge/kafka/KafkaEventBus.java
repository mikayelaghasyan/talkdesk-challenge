package talkdesk.challenge.kafka;

import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.common.KafkaClientOptions;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.producer.KafkaHeader;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import talkdesk.challenge.core.domainevent.DomainEvent;
import talkdesk.challenge.core.domainevent.DomainEventBus;
import talkdesk.challenge.core.domainevent.EventContext;
import talkdesk.challenge.core.domainevent.EventSubscriber;
import talkdesk.challenge.core.runtime.ApplicationContext;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class KafkaEventBus implements DomainEventBus {
  private static final Logger log = LoggerFactory.getLogger(KafkaEventBus.class);

  private final ApplicationContext applicationContext;
  private final JsonObject config;

  private final KafkaProducer<String, String> producer;

  public KafkaEventBus(ApplicationContext applicationContext, JsonObject config) {
    this.applicationContext = applicationContext;
    this.config = config;

    producer = KafkaProducer.createShared(applicationContext.vertx(), "default", producerConfig(config));
  }

  @Override
  public <U extends DomainEvent> Future<Void> publish(String address, U event) {
    log.debug("sent: ({}, {})", address, event);
    var record = KafkaProducerRecord
      .create(address, event.uuid().toString(), Json.encode(event))
      .addHeader(headerFor(event));
    return producer.send(record)
      .onSuccess(x -> log.debug("Success"))
      .onFailure(e -> log.error(e.getLocalizedMessage()))
      .map(x -> null);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <U> void subscribe(String address, EventSubscriber<U> handler) {
    log.debug("subscribed: {}", address);
    KafkaConsumer<String, String> consumer = KafkaConsumer
      .create(applicationContext.vertx(), consumerConfig(config));
    consumer.handler(record -> Future.succeededFuture(record.value())
      .map(body -> Json.decodeValue(body, classFromHeader(record.headers()).orElse(JsonObject.class)))
      .onSuccess(event -> log.debug("received: ({}, {})", address, event))
      .compose(event -> handler.received(createEventContext(), (U) event))
      .compose(event -> consumer.commit())
      .onFailure(error -> log.debug("failed: ({}, {})", address, error.getLocalizedMessage())));
    consumer.subscribe(address);
  }

  private KafkaClientOptions producerConfig(JsonObject config) {
    return new KafkaClientOptions()
      .setConfig("bootstrap.servers", config.getString("bootstrapServers"))
      .setConfig("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      .setConfig("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      .setConfig("acks", "-1");
  }

  private KafkaClientOptions consumerConfig(JsonObject config) {
    return new KafkaClientOptions()
      .setConfig("bootstrap.servers", config.getString("bootstrapServers"))
      .setConfig("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
      .setConfig("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
      .setConfig("group.id", config.getString("groupId"))
      .setConfig("auto.offset.reset", "earliest")
      .setConfig("enable.auto.commit", "false")
      .setConfig("allow.auto.create.topics", "true");
  }

  protected KafkaHeader headerFor(Object event) {
    return KafkaHeader.header("class", event.getClass().getCanonicalName());
  }

  protected Optional<Class<?>> classFromHeader(List<KafkaHeader> headers) {
    Optional<String> className = headers.stream()
      .filter(h -> Objects.equals(h.key(), "class"))
      .map(h -> h.value().toString()).findFirst();
    if (className.isPresent()) {
      try {
        return Optional.of(Class.forName(className.get()));
      } catch (Exception e) {
        return Optional.empty();
      }
    } else {
      return Optional.empty();
    }
  }

  private EventContext createEventContext() {
    return new EventContext(applicationContext.communicationBus(), applicationContext.eventBus(), applicationContext.dbGateway());
  }
}

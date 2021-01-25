package talkdesk.challenge.core.vertx;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import talkdesk.challenge.core.domainevent.DomainEvent;
import talkdesk.challenge.core.domainevent.DomainEventBus;
import talkdesk.challenge.core.domainevent.EventContext;
import talkdesk.challenge.core.domainevent.EventSubscriber;
import talkdesk.challenge.core.runtime.ApplicationContext;

public class VertxEventBus extends VertxBus implements DomainEventBus {
  private static final Logger log = LoggerFactory.getLogger(VertxEventBus.class);
  private final ApplicationContext applicationContext;

  public VertxEventBus(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public <U extends DomainEvent> Future<Void> publish(String address, U event) {
    log.debug("sent: ({}, {})", address, event);
    applicationContext.vertx().eventBus().publish(address, Json.encodeToBuffer(event), optionsFor(event));
    return Future.succeededFuture();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <U> void subscribe(String address, EventSubscriber<U> handler) {
    log.debug("subscribed: {}", address);
    applicationContext.vertx().eventBus().<Buffer>consumer(address, message ->
      Future.succeededFuture(message.body())
        .map(body -> Json.decodeValue(body, classFromHeader(message.headers()).orElse(JsonObject.class)))
        .onSuccess(event -> log.debug("received: ({}, {})", address, event))
        .onSuccess(event -> handler.received(createEventContext(), (U)event))
        .onFailure(error -> log.debug("failed: ({}, {})", address, error.getLocalizedMessage())));
  }

  private EventContext createEventContext() {
    return new EventContext(applicationContext.communicationBus(), applicationContext.eventBus(), applicationContext.dbGateway());
  }
}

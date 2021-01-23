package talkdesk.challenge.core.runtime;

import io.vertx.core.json.JsonObject;
import talkdesk.challenge.core.domainevent.DomainEventBus;
import talkdesk.challenge.core.domainevent.vertx.VertxEventBus;

public class DomainEventBusFactory {
  private final ApplicationContext applicationContext;

  public DomainEventBusFactory(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  public DomainEventBus createEventBus(JsonObject config) {
    throw new UnsupportedOperationException();
  }

  public DomainEventBus createDefaultEventBus() {
    return new VertxEventBus(applicationContext);
  }
}

package talkdesk.challenge.core.runtime;

import io.vertx.core.json.JsonObject;
import talkdesk.challenge.core.domainevent.DomainEventBus;
import talkdesk.challenge.core.vertx.VertxEventBus;

public class DomainEventBusFactory {
  private final ApplicationContext applicationContext;

  public DomainEventBusFactory(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @SuppressWarnings("unchecked")
  public DomainEventBus createEventBus(JsonObject config) {
    try {
      if (config.containsKey("class")) {
        String className = config.getString("class");
        Class<DomainEventBus> cls = (Class<DomainEventBus>) Class.forName(className);
        return cls.getConstructor(ApplicationContext.class, JsonObject.class).newInstance(applicationContext, config);
      } else {
        throw new RuntimeException("Domain event bus class not specified");
      }
    } catch (Exception e) {
      throw new RuntimeException(String.format("Can't load domain event bus: %s", e.getLocalizedMessage()));
    }
  }

  public DomainEventBus createDefaultEventBus() {
    return new VertxEventBus(applicationContext);
  }
}

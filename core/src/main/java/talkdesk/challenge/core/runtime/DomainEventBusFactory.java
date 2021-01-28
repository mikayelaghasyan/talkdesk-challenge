package talkdesk.challenge.core.runtime;

import io.vertx.core.json.JsonObject;
import talkdesk.challenge.core.domainevent.DomainEventBus;
import talkdesk.challenge.core.vertx.VertxEventBus;

import java.util.Optional;

public class DomainEventBusFactory {
  private final ApplicationContext applicationContext;

  public DomainEventBusFactory(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @SuppressWarnings("unchecked")
  public Optional<DomainEventBus> createEventBus(JsonObject config) {
    try {
      if (config.containsKey("class")) {
        String className = config.getString("class");
        Class<DomainEventBus> cls = (Class<DomainEventBus>) Class.forName(className);
        return Optional.of(cls.getConstructor(ApplicationContext.class, JsonObject.class).newInstance(applicationContext, config));
      } else {
        return Optional.empty();
      }
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public DomainEventBus createDefaultEventBus() {
    return new VertxEventBus(applicationContext);
  }
}

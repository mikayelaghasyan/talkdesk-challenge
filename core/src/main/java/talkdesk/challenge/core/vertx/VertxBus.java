package talkdesk.challenge.core.vertx;

import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;

import java.util.Optional;

public abstract class VertxBus {
  protected DeliveryOptions optionsFor(Object event) {
    return new DeliveryOptions().addHeader("class", event.getClass().getCanonicalName());
  }

  protected Optional<Class<?>> classFromHeader(MultiMap headers) {
    if (headers.contains("class")) {
      try {
        return Optional.of(Class.forName(headers.get("class")));
      } catch (Exception e) {
        return Optional.empty();
      }
    } else {
      return Optional.empty();
    }
  }
}

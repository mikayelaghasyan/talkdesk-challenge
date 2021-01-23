package talkdesk.challenge.core.domainevent;

import io.vertx.core.Future;

public interface DomainEventBus {
  Future<Void> publish(DomainEvent event);
  <U> void subscribe(String address, EventHandler<U> handler);
}

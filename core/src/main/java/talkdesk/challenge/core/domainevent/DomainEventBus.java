package talkdesk.challenge.core.domainevent;

import io.vertx.core.Future;

public interface DomainEventBus {
  Future<Void> publish(String address, DomainEvent event);
  <U> void subscribe(String address, EventSubscriber<U> handler);
}

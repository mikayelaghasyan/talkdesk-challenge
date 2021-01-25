package talkdesk.challenge.core.domainevent;

import io.vertx.core.Future;

public interface DomainEventBus {
  <U extends DomainEvent> Future<Void> publish(String address, U event);
  <U> void subscribe(String address, EventSubscriber<U> handler);
}

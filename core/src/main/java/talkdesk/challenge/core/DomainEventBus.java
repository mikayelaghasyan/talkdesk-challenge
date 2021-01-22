package talkdesk.challenge.core;

import io.vertx.core.Future;

public interface DomainEventBus {
  Future<Void> publish(DomainEvent event);
}

package talkdesk.challenge.core;

import io.vertx.core.Future;

public interface CommunicationBus {
  <U, V> Future<V> ask(String address, U query, Class<V> resultClass);
  <U> Future<Void> order(String address, U command);
}

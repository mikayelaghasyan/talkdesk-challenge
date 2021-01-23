package talkdesk.challenge.core.communication;

import io.vertx.core.Future;

public interface CommunicationBus {
  <U, V> Future<V> ask(String address, U query, Class<V> resultClass);
  <U> Future<Void> order(String address, U command);

  <U, V> void replyTo(String address, QueryHandler<U, V> handler);
  <U> void obeyTo(String address, CommandHandler<U> handler);
}

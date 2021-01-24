package talkdesk.challenge.core.communication;

import io.vertx.core.Future;

public abstract class QueryHandler<U, V> {
  public abstract Future<V> handle(QueryContext context, U input);
}

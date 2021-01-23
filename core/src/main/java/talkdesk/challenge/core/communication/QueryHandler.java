package talkdesk.challenge.core.communication;

import io.vertx.core.Future;

public abstract class QueryHandler<U, V> extends RequestHandler<U, V> {
  public QueryHandler(Class<U> inputClass) {
    super(inputClass);
  }

  public abstract Future<V> handle(QueryContext context, U input);
}

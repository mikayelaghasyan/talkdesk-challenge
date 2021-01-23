package talkdesk.challenge.core.runtime;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public abstract class Node extends AbstractVerticle {
  protected abstract Future<Void> run(RuntimeContext context);
}

package talkdesk.challenge.core.domainevent;

import io.vertx.core.Future;

public abstract class EventSubscriber<U> {
  public abstract Future<Void> received(EventContext context, U event);
}

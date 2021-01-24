package talkdesk.challenge.core.domainevent;

public abstract class EventSubscriber<U> {
  public abstract void received(EventContext context, U event);
}

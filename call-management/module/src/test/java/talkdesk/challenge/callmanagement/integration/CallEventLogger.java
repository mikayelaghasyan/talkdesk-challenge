package talkdesk.challenge.callmanagement.integration;

import talkdesk.challenge.callmanagement.api.event.CallEvent;
import talkdesk.challenge.core.domainevent.EventContext;
import talkdesk.challenge.core.domainevent.EventSubscriber;

import java.util.ArrayList;
import java.util.List;

public class CallEventLogger extends EventSubscriber<CallEvent> {
  private final List<CallEvent> events = new ArrayList<>();

  public List<CallEvent> events() {
    return this.events;
  }

  @Override
  public void received(EventContext context, CallEvent event) {
    events.add(event);
  }
}

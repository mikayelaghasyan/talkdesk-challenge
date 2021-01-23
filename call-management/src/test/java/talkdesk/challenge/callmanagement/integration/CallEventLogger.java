package talkdesk.challenge.callmanagement.integration;

import talkdesk.challenge.callmanagement.event.CallEvent;
import talkdesk.challenge.core.domainevent.EventHandler;

import java.util.List;

public class CallEventLogger extends EventHandler<CallEvent> {
  private List<CallEvent> events;

  public List<CallEvent> events() {
    return this.events;
  }
}

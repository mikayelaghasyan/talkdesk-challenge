package talkdesk.challenge.core.domainevent;

import talkdesk.challenge.core.communication.CommunicationBus;

public class EventContext {
  private final CommunicationBus communicationBus;

  public EventContext(CommunicationBus communicationBus) {
    this.communicationBus = communicationBus;
  }

  public CommunicationBus communicationBus() {
    return communicationBus;
  }
}

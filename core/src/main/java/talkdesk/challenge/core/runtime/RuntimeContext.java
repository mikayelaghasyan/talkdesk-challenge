package talkdesk.challenge.core.runtime;

import talkdesk.challenge.core.communication.CommunicationBus;
import talkdesk.challenge.core.domainevent.DomainEventBus;

public class RuntimeContext {
  private CommunicationBus communicationBus;
  private DomainEventBus eventBus;

  public CommunicationBus communicationBus() {
    return communicationBus;
  }

  public DomainEventBus eventBus() {
    return eventBus;
  }

  public void communicationBus(CommunicationBus communicationBus) {
    this.communicationBus = communicationBus;
  }

  public void eventBus(DomainEventBus eventBus) {
    this.eventBus = eventBus;
  }
}

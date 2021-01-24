package talkdesk.challenge.core.runtime;

import io.vertx.core.json.JsonObject;
import talkdesk.challenge.core.communication.CommunicationBus;
import talkdesk.challenge.core.domainevent.DomainEventBus;

public class RuntimeContext {
  private CommunicationBus communicationBus;
  private DomainEventBus eventBus;
  private JsonObject config;

  public CommunicationBus communicationBus() {
    return communicationBus;
  }

  public void communicationBus(CommunicationBus communicationBus) {
    this.communicationBus = communicationBus;
  }

  public DomainEventBus eventBus() {
    return eventBus;
  }

  public void eventBus(DomainEventBus eventBus) {
    this.eventBus = eventBus;
  }

  public JsonObject config() {
    return config;
  }

  public void config(JsonObject config) {
    this.config = config;
  }
}

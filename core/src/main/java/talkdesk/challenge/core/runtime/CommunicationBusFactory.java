package talkdesk.challenge.core.runtime;

import io.vertx.core.json.JsonObject;
import talkdesk.challenge.core.communication.CommunicationBus;
import talkdesk.challenge.core.vertx.VertxCommunicationBus;

public class CommunicationBusFactory {
  private final ApplicationContext applicationContext;

  public CommunicationBusFactory(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  public CommunicationBus createCommunicationBus(JsonObject config) {
    throw new UnsupportedOperationException();
  }

  public CommunicationBus createDefaultCommunicationBus() {
    return new VertxCommunicationBus(applicationContext);
  }
}

package talkdesk.challenge.core.runtime;

import io.vertx.core.json.JsonObject;
import talkdesk.challenge.core.communication.CommunicationBus;
import talkdesk.challenge.core.vertx.VertxCommunicationBus;

import java.util.Optional;

public class CommunicationBusFactory {
  private final ApplicationContext applicationContext;

  public CommunicationBusFactory(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  public Optional<CommunicationBus> createCommunicationBus(JsonObject config) {
    return Optional.empty();
  }

  public CommunicationBus createDefaultCommunicationBus() {
    return new VertxCommunicationBus(applicationContext);
  }
}

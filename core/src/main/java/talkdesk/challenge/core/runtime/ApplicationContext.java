package talkdesk.challenge.core.runtime;

import io.vertx.core.Vertx;
import talkdesk.challenge.core.communication.CommunicationBus;
import talkdesk.challenge.core.db.DbGateway;
import talkdesk.challenge.core.domainevent.DomainEventBus;

public interface ApplicationContext {
  Vertx vertx();
  CommunicationBus communicationBus();
  DomainEventBus eventBus();
  DbGateway dbGateway();
}

package talkdesk.challenge.core.domainevent;

import talkdesk.challenge.core.communication.CommunicationBus;
import talkdesk.challenge.core.db.DbGateway;
import talkdesk.challenge.core.db.ReadWriteRepository;

import java.util.HashMap;
import java.util.Map;

public class EventContext {
  private final CommunicationBus communicationBus;
  private final DomainEventBus eventBus;
  private final DbGateway dbGateway;
  private Map<String, ReadWriteRepository<?>> repositories;

  public EventContext(CommunicationBus communicationBus, DomainEventBus eventBus, DbGateway dbGateway) {
    this.communicationBus = communicationBus;
    this.eventBus = eventBus;
    this.dbGateway = dbGateway;
    this.repositories = new HashMap<>();
  }

  public CommunicationBus communicationBus() {
    return communicationBus;
  }

  public DomainEventBus eventBus() {
    return eventBus;
  }

  @SuppressWarnings("unchecked")
  public <U> ReadWriteRepository<U> repositoryOf(String name, Class<U> itemClass) {
    return (ReadWriteRepository<U>) repositories
      .computeIfAbsent(name, k -> new ReadWriteRepository<>(k, dbGateway, itemClass));
  }
}

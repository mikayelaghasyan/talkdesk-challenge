package talkdesk.challenge.core.communication;

import talkdesk.challenge.core.db.DbGateway;
import talkdesk.challenge.core.db.ReadWriteRepository;
import talkdesk.challenge.core.domainevent.DomainEventBus;

import java.util.HashMap;
import java.util.Map;

public class CommandContext {
  private final CommunicationBus communicationBus;
  private final DomainEventBus eventBus;
  private final DbGateway dbGateway;
  private final Map<String, ReadWriteRepository<?>> repositories;

  public CommandContext(CommunicationBus communicationBus, DomainEventBus eventBus, DbGateway dbGateway) {
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

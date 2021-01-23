package talkdesk.challenge.core.communication;

import talkdesk.challenge.core.db.ReadWriteRepository;
import talkdesk.challenge.core.domainevent.DomainEventBus;

import java.util.HashMap;
import java.util.Map;

public class CommandContext {
  private final CommunicationBus communicationBus;
  private final DomainEventBus eventBus;
  private Map<String, ReadWriteRepository<?>> repositories;

  public CommandContext(CommunicationBus communicationBus, DomainEventBus eventBus) {
    this.communicationBus = communicationBus;
    this.eventBus = eventBus;
    this.repositories = new HashMap<>();
  }

  public CommunicationBus communicationBus() {
    return communicationBus;
  }

  public DomainEventBus eventBus() {
    return eventBus;
  }

  @SuppressWarnings("unchecked")
  public <U> ReadWriteRepository<U> repositoryOf(String name) {
    return (ReadWriteRepository<U>) repositories
      .computeIfAbsent(name, k -> new ReadWriteRepository<U>(name));
  }
}

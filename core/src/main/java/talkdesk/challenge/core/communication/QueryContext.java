package talkdesk.challenge.core.communication;

import talkdesk.challenge.core.db.ReadOnlyRepository;

import java.util.HashMap;
import java.util.Map;

public class QueryContext {
  private final CommunicationBus communicationBus;
  private Map<String, ReadOnlyRepository<?>> repositories;

  public QueryContext(CommunicationBus communicationBus) {
    this.communicationBus = communicationBus;
    this.repositories = new HashMap<>();
  }

  public CommunicationBus communicationBus() {
    return communicationBus;
  }

  @SuppressWarnings("unchecked")
  public <U> ReadOnlyRepository<U> repositoryOf(String name) {
    return (ReadOnlyRepository<U>) repositories
      .computeIfAbsent(name, k -> new ReadOnlyRepository<U>(name));
  }
}

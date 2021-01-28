package talkdesk.challenge.core.communication;

import talkdesk.challenge.core.db.DbGateway;
import talkdesk.challenge.core.db.ReadOnlyRepository;

import java.util.HashMap;
import java.util.Map;

public class QueryContext {
  private final CommunicationBus communicationBus;
  private final DbGateway dbGateway;
  private final Map<String, ReadOnlyRepository<?>> repositories;

  public QueryContext(CommunicationBus communicationBus, DbGateway dbGateway) {
    this.communicationBus = communicationBus;
    this.dbGateway = dbGateway;
    this.repositories = new HashMap<>();
  }

  public CommunicationBus communicationBus() {
    return communicationBus;
  }

  @SuppressWarnings("unchecked")
  public <U> ReadOnlyRepository<U> repositoryOf(String name, Class<U> itemClass) {
    return (ReadOnlyRepository<U>) repositories
      .computeIfAbsent(name, k -> new ReadOnlyRepository<>(k, dbGateway, itemClass));
  }
}

package talkdesk.challenge.core.db;

public class ReadOnlyRepository<U> {
  protected final String name;
  protected final DbGateway dbGateway;

  public ReadOnlyRepository(String name, DbGateway dbGateway) {
    this.name = name;
    this.dbGateway = dbGateway;
  }
}

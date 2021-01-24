package talkdesk.challenge.core.runtime;

import io.vertx.core.json.JsonObject;
import talkdesk.challenge.core.db.DbGateway;
import talkdesk.challenge.core.db.InMemoryDbGateway;

public class DbGatewayFactory {
  private final ApplicationContext applicationContext;

  public DbGatewayFactory(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  public DbGateway createDbGateway(JsonObject config) {
    throw new UnsupportedOperationException();
  }

  public DbGateway createDefaultDbGateway() {
    return new InMemoryDbGateway(applicationContext);
  }
}

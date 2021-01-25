package talkdesk.challenge.core.runtime;

import io.vertx.core.json.JsonObject;
import talkdesk.challenge.core.db.DbGateway;
import talkdesk.challenge.core.db.InMemoryDbGateway;

public class DbGatewayFactory {
  private final ApplicationContext applicationContext;

  public DbGatewayFactory(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @SuppressWarnings("unchecked")
  public DbGateway createDbGateway(JsonObject config) {
    try {
      if (config.containsKey("class")) {
        String className = config.getString("class");
        Class<DbGateway> cls = (Class<DbGateway>) Class.forName(className);
        return cls.getConstructor(ApplicationContext.class, JsonObject.class).newInstance(applicationContext, config);
      } else {
        throw new RuntimeException("DB gateway class not specified");
      }
    } catch (Exception e) {
      throw new RuntimeException(String.format("Can't load DB gateway: %s", e.getLocalizedMessage()));
    }
  }

  public DbGateway createDefaultDbGateway() {
    return new InMemoryDbGateway(applicationContext);
  }
}

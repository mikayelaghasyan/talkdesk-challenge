package talkdesk.challenge.core.runtime;

import io.vertx.core.json.JsonObject;
import talkdesk.challenge.core.db.DbGateway;
import talkdesk.challenge.core.db.InMemoryDbGateway;

import java.util.Optional;

public class DbGatewayFactory {
  private final ApplicationContext applicationContext;

  public DbGatewayFactory(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @SuppressWarnings("unchecked")
  public Optional<DbGateway> createDbGateway(JsonObject config) {
    try {
      if (config.containsKey("class")) {
        String className = config.getString("class");
        Class<DbGateway> cls = (Class<DbGateway>) Class.forName(className);
        return Optional.of(cls.getConstructor(ApplicationContext.class, JsonObject.class).newInstance(applicationContext, config));
      } else {
        return Optional.empty();
      }
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  public DbGateway createDefaultDbGateway() {
    return new InMemoryDbGateway(applicationContext);
  }
}

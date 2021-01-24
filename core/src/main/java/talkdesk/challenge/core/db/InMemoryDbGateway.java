package talkdesk.challenge.core.db;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import talkdesk.challenge.core.runtime.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryDbGateway implements DbGateway {
  private final ApplicationContext applicationContext;

  private Map<String, List<JsonObject>> dataMap = new HashMap<>();

  public InMemoryDbGateway(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public Future<JsonObject> save(String name, JsonObject obj) {
    dataMap.computeIfAbsent(name, k -> new ArrayList<>())
      .add(obj);
    return Future.succeededFuture(obj);
  }
}

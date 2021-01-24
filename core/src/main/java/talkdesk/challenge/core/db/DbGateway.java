package talkdesk.challenge.core.db;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public interface DbGateway {
  Future<JsonObject> save(String name, JsonObject obj);
}

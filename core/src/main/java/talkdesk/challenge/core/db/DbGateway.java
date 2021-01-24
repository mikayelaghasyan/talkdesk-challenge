package talkdesk.challenge.core.db;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import talkdesk.challenge.core.model.Order;
import talkdesk.challenge.core.model.Page;

public interface DbGateway {
  Future<JsonObject> save(String name, JsonObject obj);

  Future<JsonArray> findMany(String name, Condition query, Page page, Order order);

  Future<Long> count(String name, Condition query);
}

package talkdesk.challenge.core.db;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import talkdesk.challenge.core.model.Order;
import talkdesk.challenge.core.model.Page;

import java.util.UUID;

public interface DbGateway {
  Future<JsonObject> save(String name, JsonObject obj);
  Future<Void> delete(String name, UUID uuid);

  Future<JsonArray> findMany(String name, Condition query, Page page, Order order);

  Future<Long> count(String name, Condition query);
}

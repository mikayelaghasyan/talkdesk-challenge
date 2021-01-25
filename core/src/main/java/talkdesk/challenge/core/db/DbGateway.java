package talkdesk.challenge.core.db;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import talkdesk.challenge.core.db.condition.Condition;
import talkdesk.challenge.core.model.Order;
import talkdesk.challenge.core.model.Page;

import java.util.Optional;
import java.util.UUID;

public interface DbGateway {
  Future<JsonObject> save(String name, JsonObject obj);
  Future<Boolean> delete(String name, UUID uuid);
  Future<Boolean> deleteAll(String name);

  Future<Optional<JsonObject>> findOne(String name, UUID uuid);
  Future<Optional<JsonObject>> findFirst(String name, Condition condition);
  Future<JsonArray> findMany(String name, Condition condition, Page page, Order order);

  Future<Long> count(String name, Condition condition);
}

package talkdesk.challenge.mongo;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import talkdesk.challenge.core.db.DbGateway;
import talkdesk.challenge.core.db.condition.*;
import talkdesk.challenge.core.model.Order;
import talkdesk.challenge.core.model.Page;
import talkdesk.challenge.core.runtime.ApplicationContext;

import java.util.Optional;
import java.util.UUID;

public class MongoDbGateway implements DbGateway {
  private final MongoClient client;

  public MongoDbGateway(ApplicationContext applicationContext, JsonObject config) {
    this.client = MongoClient.createShared(applicationContext.vertx(), config);
  }

  @Override
  public Future<JsonObject> save(String name, JsonObject obj) {
    FindOptions findOptions = new FindOptions();
    UpdateOptions updateOptions = new UpdateOptions(true).setReturningNewDocument(true);
    JsonObject query = new JsonObject().put("uuid", obj.getString("uuid"));
    return client.findOneAndReplaceWithOptions(name, query, obj, findOptions, updateOptions);
  }

  @Override
  public Future<Boolean> delete(String name, UUID uuid) {
    JsonObject query = new JsonObject().put("uuid", uuid.toString());
    return client.removeDocument(name, query)
      .map(x -> x.getRemovedCount() > 0);
  }

  @Override
  public Future<Boolean> deleteAll(String name) {
    return client.removeDocuments(name, new JsonObject())
      .map(x -> x.getRemovedCount() > 0);
  }

  @Override
  public Future<Optional<JsonObject>> findOne(String name, UUID uuid) {
    JsonObject query = new JsonObject().put("uuid", uuid.toString());
    return client.find(name, query)
      .map(x -> x.stream().findFirst());
  }

  @Override
  public Future<Optional<JsonObject>> findFirst(String name, Condition condition) {
    JsonObject query = buildQuery(condition);
    return client.find(name, query)
      .map(x -> x.stream().findFirst());
  }

  @Override
  public Future<JsonArray> findMany(String name, Condition condition, Page page, Order order) {
    JsonObject query = buildQuery(condition);
    FindOptions options = new FindOptions();
    if (order != null) {
      options.setSort(new JsonObject()
        .put(order.field(), Order.Direction.DESCENDING.equals(order.direction()) ? -1 : 1));
    }
    if (page != null) {
      options.setSkip((int) (page.number() * page.size()));
      options.setLimit((int) page.size());
    }
    return client.findWithOptions(name, query, options)
      .map(x -> new JsonArray(x));
  }

  @Override
  public Future<Long> count(String name, Condition condition) {
    JsonObject query = buildQuery(condition);
    return client.count(name, query);
  }

  private JsonObject buildQuery(Condition condition) {
    if (condition instanceof Truth) {
      return new JsonObject();
    } else if (condition instanceof BinaryOperator) {
      BinaryOperator operator = (BinaryOperator) condition;
      if (operator instanceof And) {
        return buildQuery(operator.left)
          .mergeIn(buildQuery(operator.right), true);
      } else if (operator instanceof Or) {
        return new JsonObject()
          .put("$or", new JsonArray()
            .add(buildQuery(operator.left))
            .add(buildQuery(operator.right)));
      }
    } else if (condition instanceof UnaryOperator) {
      UnaryOperator operator = (UnaryOperator) condition;
      if (operator instanceof Not) {
        return new JsonObject()
          .put("$not", buildQuery(operator.operand));
      }
    } else if (condition instanceof Comparison) {
      Comparison comparison = (Comparison) condition;
      String op = null;
      if (comparison instanceof Eq) {
        op = "$eq";
      } else if (comparison instanceof Gt) {
        op = "$gt";
      } else if (comparison instanceof Lt) {
        op = "$lt";
      }
      return new JsonObject()
        .put(comparison.field, new JsonObject()
          .put(op, comparison.value));
    }
    return new JsonObject();
  }
}

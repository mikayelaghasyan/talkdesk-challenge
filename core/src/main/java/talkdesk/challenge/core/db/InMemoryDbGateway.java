package talkdesk.challenge.core.db;

import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import talkdesk.challenge.core.db.condition.*;
import talkdesk.challenge.core.model.Order;
import talkdesk.challenge.core.model.Page;
import talkdesk.challenge.core.runtime.ApplicationContext;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryDbGateway implements DbGateway {
  private final ApplicationContext applicationContext;

  private final Map<String, Map<UUID, JsonObject>> dataMap = new HashMap<>();

  public InMemoryDbGateway(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public Future<JsonObject> save(String name, JsonObject obj) {
    UUID uuid = UUID.fromString(obj.getString("uuid"));
    dataMap.computeIfAbsent(name, k -> new HashMap<>()).put(uuid, obj);
    return Future.succeededFuture(obj);
  }

  @Override
  public Future<Boolean> delete(String name, UUID uuid) {
    JsonObject obj = dataMap.computeIfAbsent(name, k -> new HashMap<>()).remove(uuid);
    return Future.succeededFuture(!Objects.isNull(obj));
  }

  @Override
  public Future<Boolean> deleteAll(String name) {
    dataMap.computeIfAbsent(name, k -> new HashMap<>()).clear();
    return Future.succeededFuture(true);
  }

  @Override
  public Future<Optional<JsonObject>> findOne(String name, UUID uuid) {
    JsonObject obj = dataMap.getOrDefault(name, Collections.emptyMap()).get(uuid);
    return Future.succeededFuture(Optional.ofNullable(obj));
  }

  @Override
  public Future<Optional<JsonObject>> findFirst(String name, Condition condition) {
    Collection<JsonObject> data = dataMap.getOrDefault(name, Collections.emptyMap()).values();
    if (condition != null) {
      data = filtered(data, condition);
    }
    Optional<JsonObject> obj = data.stream().findFirst();
    return Future.succeededFuture(obj);
  }

  @Override
  public Future<JsonArray> findMany(String name, Condition condition, Page page, Order order) {
    Collection<JsonObject> data = dataMap.getOrDefault(name, Collections.emptyMap()).values();
    if (condition != null) {
      data = filtered(data, condition);
    }
    if (order != null) {
      data = sorted(data, order);
    }
    if (page != null) {
      data = data.stream().skip(page.number() * page.size()).limit(page.size()).collect(Collectors.toList());
    }
    return Future.succeededFuture(new JsonArray(new ArrayList<>(data)));
  }

  @Override
  public Future<Long> count(String name, Condition condition) {
    Collection<JsonObject> data = dataMap.getOrDefault(name, Collections.emptyMap()).values();
    if (condition != null) {
      data = filtered(data, condition);
    }
    return Future.succeededFuture((long) data.size());
  }

  private Collection<JsonObject> filtered(Collection<JsonObject> items, Condition query) {
    return items.stream()
      .filter(item -> evaluate(item, query))
      .collect(Collectors.toList());
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private Collection<JsonObject> sorted(Collection<JsonObject> items, Order order) {
    Comparator<JsonObject> comparator = Comparator.comparing((JsonObject x) -> (Comparable)x.getValue(order.field()));
    if (Order.Direction.DESCENDING.equals(order.direction())) {
      comparator = comparator.reversed();
    }
    return items.stream().sorted(comparator).collect(Collectors.toList());
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private boolean evaluate(JsonObject item, Condition condition) {
    if (condition instanceof Truth) {
      return true;
    } else if (condition instanceof BinaryOperator) {
      BinaryOperator operator = (BinaryOperator) condition;
      if (operator instanceof And) {
        return evaluate(item, operator.left) && evaluate(item, operator.right);
      } else if (operator instanceof Or) {
        return evaluate(item, operator.left) || evaluate(item, operator.right);
      }
    } else if (condition instanceof UnaryOperator) {
      UnaryOperator operator = (UnaryOperator) condition;
      if (operator instanceof Not) {
        return !evaluate(item, operator.operand);
      }
    } else if (condition instanceof Comparison) {
      Comparison comparison = (Comparison) condition;
      var value = item.getValue(comparison.field);
      Comparable field = (Comparable) Json.CODEC.fromValue(value, comparison.value.getClass());
      int comparisonResult = field.compareTo(comparison.value);
      if (comparison instanceof Eq) {
        return comparisonResult == 0;
      } else if (comparison instanceof Gt) {
        return comparisonResult > 0;
      } else if (comparison instanceof Lt) {
        return comparisonResult < 0;
      }
    }
    return false;
  }
}

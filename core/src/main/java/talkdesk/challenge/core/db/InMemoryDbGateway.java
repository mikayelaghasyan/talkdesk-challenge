package talkdesk.challenge.core.db;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import talkdesk.challenge.core.model.Order;
import talkdesk.challenge.core.model.Page;
import talkdesk.challenge.core.runtime.ApplicationContext;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryDbGateway implements DbGateway {
  private final ApplicationContext applicationContext;

  private final Map<String, List<JsonObject>> dataMap = new HashMap<>();

  public InMemoryDbGateway(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public Future<JsonObject> save(String name, JsonObject obj) {
    dataMap.computeIfAbsent(name, k -> new ArrayList<>())
      .add(obj);
    return Future.succeededFuture(obj);
  }

  @Override
  public Future<JsonArray> findMany(String name, Condition query, Page page, Order order) {
    List<JsonObject> data = dataMap.getOrDefault(name, Collections.emptyList());
    if (query != null) {
      data = filtered(data, query);
    }
    if (order != null) {
      data = sorted(data, order);
    }
    if (page != null) {
      data = data.stream().skip(page.number() * page.size()).limit(page.size()).collect(Collectors.toList());
    }
    return Future.succeededFuture(new JsonArray(data));
  }

  @Override
  public Future<Long> count(String name, Condition query) {
    List<JsonObject> data = dataMap.getOrDefault(name, Collections.emptyList());
    if (query != null) {
      data = filtered(data, query);
    }
    return Future.succeededFuture((long) data.size());
  }

  private List<JsonObject> filtered(List<JsonObject> items, Condition query) {
    return items.stream()
      .filter(item -> evaluate(item, query))
      .collect(Collectors.toList());
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private List<JsonObject> sorted(List<JsonObject> items, Order order) {
    Comparator<JsonObject> comparator = Comparator.comparing((JsonObject x) -> (Comparable)x.getValue(order.field()));
    if (Order.Direction.DESCENDING.equals(order.direction())) {
      comparator = comparator.reversed();
    }
    return items.stream().sorted(comparator).collect(Collectors.toList());
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private boolean evaluate(JsonObject item, Condition query) {
    if (query instanceof Truth) {
      return true;
    } else if (query instanceof Falsity) {
      return false;
    } else if (query instanceof BinaryOperator) {
      BinaryOperator operator = (BinaryOperator) query;
      if (operator instanceof And) {
        return evaluate(item, operator.left) && evaluate(item, operator.right);
      } else if (operator instanceof Or) {
        return evaluate(item, operator.left) || evaluate(item, operator.right);
      }
    } else if (query instanceof UnaryOperator) {
      UnaryOperator operator = (UnaryOperator) query;
      if (operator instanceof Not) {
        return !evaluate(item, operator.operand);
      }
    } else if (query instanceof Comparison) {
      Comparison comparison = (Comparison) query;
      Comparable field = (Comparable) item.getValue(comparison.field);
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

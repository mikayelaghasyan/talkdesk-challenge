package talkdesk.challenge.core.db;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import talkdesk.challenge.core.model.Order;
import talkdesk.challenge.core.model.Page;

import java.util.List;
import java.util.stream.Collectors;

public class ReadOnlyRepository<U> {
  protected final String name;
  protected final DbGateway dbGateway;
  protected final Class<U> itemClass;

  public ReadOnlyRepository(String name, DbGateway dbGateway, Class<U> itemClass) {
    this.name = name;
    this.dbGateway = dbGateway;
    this.itemClass = itemClass;
  }

  public Future<List<U>> findMany(Condition query, Page page, Order order) {
    return dbGateway.findMany(name, query, page, order)
      .map(result -> result.stream()
        .map(item -> ((JsonObject)item).mapTo(itemClass))
        .collect(Collectors.toList()));
  }

  public Future<Long> count(Condition query) {
    return dbGateway.count(name, query);
  }
}

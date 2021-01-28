package talkdesk.challenge.core.db;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import talkdesk.challenge.core.error.NotFound;

import java.util.UUID;

public class ReadWriteRepository<U> extends ReadOnlyRepository<U> {
  public ReadWriteRepository(String name, DbGateway dbGateway, Class<U> itemClass) {
    super(name, dbGateway, itemClass);
  }

  public Future<U> save(U obj) {
    return dbGateway.save(name, JsonObject.mapFrom(obj))
      .map(saved -> saved.mapTo(itemClass));
  }

  public Future<Void> delete(UUID uuid) {
    return dbGateway.delete(name, uuid)
      .compose(deleted -> !deleted ? Future.failedFuture(new NotFound(uuid)) : Future.succeededFuture());
  }
}

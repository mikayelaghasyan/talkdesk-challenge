package talkdesk.challenge.core.db;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public class ReadWriteRepository<U> extends ReadOnlyRepository<U> {
  public ReadWriteRepository(String name, DbGateway dbGateway) {
    super(name, dbGateway);
  }

  @SuppressWarnings("unchecked")
  public Future<U> save(U obj) {
    return dbGateway.save(name, JsonObject.mapFrom(obj))
      .map(saved -> (U)saved.mapTo(obj.getClass()));
  }
}

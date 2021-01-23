package talkdesk.challenge.core.db;

import io.vertx.core.Future;

public class ReadWriteRepository<U> extends ReadOnlyRepository<U> {
  public ReadWriteRepository(String name) {
    super(name);
  }

  public Future<U> save(U obj) {
    return Future.failedFuture(new UnsupportedOperationException());
  }
}

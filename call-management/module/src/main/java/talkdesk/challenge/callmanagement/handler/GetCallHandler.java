package talkdesk.challenge.callmanagement.handler;

import io.vertx.core.Future;
import talkdesk.challenge.callmanagement.api.model.Call;
import talkdesk.challenge.core.communication.QueryContext;
import talkdesk.challenge.core.communication.QueryHandler;

import java.util.UUID;

public class GetCallHandler extends QueryHandler<UUID, Call> {
  @Override
  public Future<Call> handle(QueryContext context, UUID uuid) {
    return context.repositoryOf("call", Call.class).findOne(uuid);
  }
}

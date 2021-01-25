package talkdesk.challenge.callmanagement.handler;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import talkdesk.challenge.callmanagement.api.model.Call;
import talkdesk.challenge.callmanagement.api.query.GetCalls;
import talkdesk.challenge.core.communication.QueryContext;
import talkdesk.challenge.core.communication.QueryHandler;
import talkdesk.challenge.core.db.condition.Condition;
import talkdesk.challenge.core.db.ReadOnlyRepository;
import talkdesk.challenge.core.model.Paginated;

import java.util.List;
import java.util.Objects;

public class GetCallsHandler extends QueryHandler<GetCalls, Paginated<Call>> {
  @Override
  public Future<Paginated<Call>> handle(QueryContext context, GetCalls query) {
    Condition condition = Condition.truth();
    if (!Objects.isNull(query.type())) {
      condition = condition.and(Condition.eq("type", query.type()));
    }
    ReadOnlyRepository<Call> repository = context.repositoryOf("call", Call.class);
    Future<List<Call>> result = repository.findMany(condition, query.page(), query.order());
    Future<Long> count = repository.count(condition);
    return CompositeFuture.all(result, count)
      .map(x -> new Paginated<>(result.result(), query.page(), count.result()));
  }
}

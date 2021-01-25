package talkdesk.challenge.callstatistics.handler;

import io.vertx.core.Future;
import talkdesk.challenge.callstatistics.api.model.Stat;
import talkdesk.challenge.callstatistics.api.query.GetStats;
import talkdesk.challenge.core.communication.QueryContext;
import talkdesk.challenge.core.communication.QueryHandler;
import talkdesk.challenge.core.db.ReadOnlyRepository;
import talkdesk.challenge.core.model.Order;

import java.util.List;

public class GetStatsHandler extends QueryHandler<GetStats, List<Stat>> {
  @Override
  public Future<List<Stat>> handle(QueryContext context, GetStats input) {
    ReadOnlyRepository<Stat> repository = context.repositoryOf("stat", Stat.class);
    return repository.findAll(null, new Order("date", Order.Direction.ASCENDING));
  }
}

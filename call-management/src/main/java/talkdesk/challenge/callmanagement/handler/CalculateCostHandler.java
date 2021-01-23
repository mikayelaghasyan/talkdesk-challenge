package talkdesk.challenge.callmanagement.handler;

import io.vertx.core.Future;
import talkdesk.challenge.callmanagement.model.Cost;
import talkdesk.challenge.callmanagement.query.CalculateCost;
import talkdesk.challenge.core.communication.QueryContext;
import talkdesk.challenge.core.communication.QueryHandler;

public class CalculateCostHandler extends QueryHandler<CalculateCost, Cost> {
  public CalculateCostHandler() {
    super(CalculateCost.class);
  }

  @Override
  public Future<Cost> handle(QueryContext context, CalculateCost input) {
    return Future.failedFuture(new UnsupportedOperationException());
  }
}

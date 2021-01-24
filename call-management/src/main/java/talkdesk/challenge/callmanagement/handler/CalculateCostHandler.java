package talkdesk.challenge.callmanagement.handler;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import talkdesk.challenge.callmanagement.model.Cost;
import talkdesk.challenge.callmanagement.model.Tariff;
import talkdesk.challenge.callmanagement.query.CalculateCost;
import talkdesk.challenge.core.communication.QueryContext;
import talkdesk.challenge.core.communication.QueryHandler;

public class CalculateCostHandler extends QueryHandler<CalculateCost, Cost> {
  private Tariff tariff;

  public CalculateCostHandler(JsonObject tariff) {
    this.tariff = tariff.mapTo(Tariff.class);
  }

  @Override
  public Future<Cost> handle(QueryContext context, CalculateCost query) {
    return Future.succeededFuture(tariff.calculateCost(query.startedAt(), query.endedAt(), query.type()));
  }

}

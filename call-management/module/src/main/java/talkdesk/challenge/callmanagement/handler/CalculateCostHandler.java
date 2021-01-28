package talkdesk.challenge.callmanagement.handler;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import talkdesk.challenge.callmanagement.api.model.Cost;
import talkdesk.challenge.callmanagement.api.model.Tariff;
import talkdesk.challenge.callmanagement.api.query.CalculateCost;
import talkdesk.challenge.core.communication.QueryContext;
import talkdesk.challenge.core.communication.QueryHandler;

public class CalculateCostHandler extends QueryHandler<CalculateCost, Cost> {
  private final Tariff tariff;

  public CalculateCostHandler(JsonObject tariff) {
    this.tariff = tariff.mapTo(Tariff.class);
  }

  @Override
  public Future<Cost> handle(QueryContext context, CalculateCost query) {
    return Future.succeededFuture(tariff.calculateCost(query.startedAt(), query.endedAt(), query.type()));
  }

}

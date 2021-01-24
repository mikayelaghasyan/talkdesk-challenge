package talkdesk.challenge.callmanagement.handler;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import talkdesk.challenge.callmanagement.command.CreateCall;
import talkdesk.challenge.callmanagement.event.CallCreated;
import talkdesk.challenge.callmanagement.model.Call;
import talkdesk.challenge.callmanagement.model.Cost;
import talkdesk.challenge.callmanagement.query.CalculateCost;
import talkdesk.challenge.core.communication.CommandContext;
import talkdesk.challenge.core.communication.CommandHandler;

public class CreateCallHandler extends CommandHandler<CreateCall> {
  public Future<Void> handle(CommandContext context, CreateCall command) {
    return Future.succeededFuture(createCalculateCostQuery(command))
      .compose(costQuery -> context.communicationBus()
        .ask("call-management.calculate-cost", costQuery, Cost.class))
      .compose(cost -> CompositeFuture.all(
        Future.succeededFuture(createCall(command))
          .compose(obj -> context.<Call>repositoryOf("call").save(obj)),
        Future.succeededFuture(createEvent(command, cost))
          .compose(event -> context.eventBus().publish("call", event))
      )).map(x -> null);
  }

  private Call createCall(CreateCall command) {
    var call = new Call();
    call.uuid(command.uuid());
    call.callerNumber(command.callerNumber());
    call.calleeNumber(command.calleeNumber());
    call.startedAt(command.startedAt());
    call.endedAt(command.endedAt());
    call.type(command.type());
    return call;
  }

  private CalculateCost createCalculateCostQuery(CreateCall command) {
    var query = new CalculateCost();
    query.startedAt(command.startedAt());
    query.endedAt(command.endedAt());
    query.type(command.type());
    return query;
  }

  private CallCreated createEvent(CreateCall command, Cost cost) {
    var event = new CallCreated();
    event.uuid(command.uuid());
    event.callerNumber(command.callerNumber());
    event.calleeNumber(command.calleeNumber());
    event.startedAt(command.startedAt());
    event.endedAt(command.endedAt());
    event.type(command.type());
    event.cost(cost);
    return event;
  }
}

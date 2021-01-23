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
  public CreateCallHandler() {
    super(CreateCall.class);
  }

  public Future<Void> handle(CommandContext context, CreateCall command) {
    return Future.succeededFuture(CalculateCost.builder().build())
      .compose(costQuery -> context.communicationBus()
        .ask("call-management.calculate-cost", costQuery, Cost.class))
      .compose(cost -> CompositeFuture.all(
        Future.succeededFuture(createObject(command))
          .compose(obj -> context.<Call>repositoryOf("call").save(obj)),
        Future.succeededFuture(createEvent(command, cost))
          .compose(event -> context.eventBus().publish(event))
      )).map(x -> null);
  }

  private Call createObject(CreateCall command) {
    return Call.builder()
      .uuid(command.uuid())
      .callerNumber(command.callerNumber())
      .calleeNumber(command.calleeNumber())
      .startedAt(command.startedAt())
      .endedAt(command.endedAt())
      .type(command.type())
      .build();
  }

  private CallCreated createEvent(CreateCall command, Cost cost) {
    return CallCreated.builder()
      .uuid(command.uuid())
      .callerNumber(command.callerNumber())
      .calleeNumber(command.calleeNumber())
      .startedAt(command.startedAt())
      .endedAt(command.endedAt())
      .type(command.type())
      .cost(cost)
      .build();
  }
}

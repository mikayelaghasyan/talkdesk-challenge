package talkdesk.challenge.callmanagement.handler;

import io.vertx.core.Future;
import talkdesk.challenge.callmanagement.command.DeleteCall;
import talkdesk.challenge.callmanagement.event.CallDeleted;
import talkdesk.challenge.callmanagement.model.Call;
import talkdesk.challenge.core.communication.CommandContext;
import talkdesk.challenge.core.communication.CommandHandler;

public class DeleteCallHandler extends CommandHandler<DeleteCall> {
  @Override
  public Future<Void> handle(CommandContext context, DeleteCall command) {
    return context.repositoryOf("call", Call.class).delete(command.uuid())
      .compose(event -> context.eventBus().publish("call", createEvent(command)));
  }

  private CallDeleted createEvent(DeleteCall command) {
    var event = new CallDeleted();
    event.uuid(command.uuid());
    return event;
  }
}

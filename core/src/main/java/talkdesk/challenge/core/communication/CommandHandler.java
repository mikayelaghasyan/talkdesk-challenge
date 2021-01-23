package talkdesk.challenge.core.communication;

import io.vertx.core.Future;

public abstract class CommandHandler<U> extends RequestHandler<U, Void> {
  public CommandHandler(Class<U> inputClass) {
    super(inputClass);
  }

  public abstract Future<Void> handle(CommandContext context, U input);
}

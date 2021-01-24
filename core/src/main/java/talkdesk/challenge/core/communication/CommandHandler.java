package talkdesk.challenge.core.communication;

import io.vertx.core.Future;

public abstract class CommandHandler<U> {
  public abstract Future<Void> handle(CommandContext context, U command);
}

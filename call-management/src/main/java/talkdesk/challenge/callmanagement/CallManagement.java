package talkdesk.challenge.callmanagement;

import io.vertx.core.Future;
import talkdesk.challenge.callmanagement.handler.CalculateCostHandler;
import talkdesk.challenge.callmanagement.handler.CreateCallHandler;
import talkdesk.challenge.core.runtime.Node;
import talkdesk.challenge.core.runtime.RuntimeContext;

public class CallManagement extends Node {
  @Override
  protected Future<Void> run(RuntimeContext context) {
    context.communicationBus().obeyTo("call-management.create-call", new CreateCallHandler());
    context.communicationBus().replyTo("call-management.calculate-cost", new CalculateCostHandler());
    return Future.succeededFuture();
  }
}

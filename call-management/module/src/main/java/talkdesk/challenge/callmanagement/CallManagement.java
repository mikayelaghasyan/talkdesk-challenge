package talkdesk.challenge.callmanagement;

import io.vertx.core.Future;
import talkdesk.challenge.callmanagement.handler.CalculateCostHandler;
import talkdesk.challenge.callmanagement.handler.CreateCallHandler;
import talkdesk.challenge.callmanagement.handler.DeleteCallHandler;
import talkdesk.challenge.callmanagement.handler.GetCallsHandler;
import talkdesk.challenge.core.runtime.Node;
import talkdesk.challenge.core.runtime.RuntimeContext;

public class CallManagement extends Node {
  @Override
  protected Future<Void> run(RuntimeContext context) {
    context.communicationBus().obeyTo("call-management.create-call", new CreateCallHandler());
    context.communicationBus().replyTo("call-management.calculate-cost", new CalculateCostHandler(context.config().getJsonObject("tariff")));
    context.communicationBus().replyTo("call-management.get-calls", new GetCallsHandler());
    context.communicationBus().obeyTo("call-management.delete-call", new DeleteCallHandler());
    return Future.succeededFuture();
  }
}

package talkdesk.challenge.callstatistics;

import io.vertx.core.Future;
import talkdesk.challenge.callstatistics.handler.CallEventHandler;
import talkdesk.challenge.callstatistics.handler.GetStatsHandler;
import talkdesk.challenge.core.runtime.Node;
import talkdesk.challenge.core.runtime.RuntimeContext;

public class CallStatistics extends Node {
  @Override
  protected Future<Void> run(RuntimeContext context) {
    context.eventBus().subscribe("call", new CallEventHandler());
    context.communicationBus().replyTo("call-statistics.get-stats", new GetStatsHandler());
    return Future.succeededFuture();
  }
}

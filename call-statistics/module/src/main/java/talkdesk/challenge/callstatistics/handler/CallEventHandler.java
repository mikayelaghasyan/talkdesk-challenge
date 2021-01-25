package talkdesk.challenge.callstatistics.handler;

import io.vertx.core.Future;
import talkdesk.challenge.callmanagement.api.event.CallCreated;
import talkdesk.challenge.callmanagement.api.event.CallDeleted;
import talkdesk.challenge.callmanagement.api.event.CallEvent;
import talkdesk.challenge.callstatistics.api.model.CallSnapshot;
import talkdesk.challenge.callstatistics.api.model.Stat;
import talkdesk.challenge.core.db.Condition;
import talkdesk.challenge.core.db.ReadWriteRepository;
import talkdesk.challenge.core.domainevent.EventContext;
import talkdesk.challenge.core.domainevent.EventSubscriber;
import talkdesk.challenge.core.error.NotFound;

public class CallEventHandler extends EventSubscriber<CallEvent> {
  @Override
  public void received(EventContext context, CallEvent event) {
    if (event instanceof CallCreated) {
      handleCallCreated(context, (CallCreated)event);
    } else if (event instanceof CallDeleted) {
      handleCallDeleted(context, (CallDeleted)event);
    }
  }

  private void handleCallCreated(EventContext context, CallCreated event) {
    ReadWriteRepository<CallSnapshot> callSnapshotRepository = context.repositoryOf("call-snapshot", CallSnapshot.class);
    ReadWriteRepository<Stat> statRepository = context.repositoryOf("stat", Stat.class);
    callSnapshotRepository.save(createCallSnapshot(event))
      .compose(callSnapshot -> Future.succeededFuture(callSnapshot.startedAt().toLocalDate())
        .compose(date -> statRepository.findFirst(Condition.eq("date", date))
          .map(item -> item.orElseGet(() -> new Stat(date)))
          .map(stat -> addCallToStat(stat, callSnapshot)))
        .compose(stat -> statRepository.save(stat)));
  }

  private CallSnapshot createCallSnapshot(CallCreated event) {
    var snapshot = new CallSnapshot();
    snapshot.uuid(event.uuid());
    snapshot.callerNumber(event.callerNumber());
    snapshot.calleeNumber(event.calleeNumber());
    snapshot.startedAt(event.startedAt());
    snapshot.endedAt(event.endedAt());
    snapshot.type(event.type());
    snapshot.cost(event.cost());
    return snapshot;
  }

  private Stat addCallToStat(Stat stat, CallSnapshot callSnapshot) {
    stat.totalDurationByCallType(callSnapshot.type()).increaseDuration(callSnapshot.duration());
    stat.incrementTotalNumberOfCalls();
    stat.numberOfCallsByCaller(callSnapshot.callerNumber()).incrementNumberOfCalls();
    stat.numberOfCallsByCallee(callSnapshot.calleeNumber()).incrementNumberOfCalls();
    stat.increaseTotalCost(callSnapshot.cost());
    return stat;
  }

  private void handleCallDeleted(EventContext context, CallDeleted event) {
    ReadWriteRepository<CallSnapshot> callSnapshotRepository = context.repositoryOf("call-snapshot", CallSnapshot.class);
    ReadWriteRepository<Stat> statRepository = context.repositoryOf("stat", Stat.class);
    callSnapshotRepository.findOne(event.uuid())
      .map(callSnapshot -> callSnapshotRepository.delete(callSnapshot.uuid())
        .map(x -> callSnapshot.startedAt().toLocalDate())
        .compose(date -> statRepository.findFirst(Condition.eq("date", date))
          .map(item -> item.orElseThrow(() -> new NotFound("Stat not found")))
          .map(stat -> removeCallFromStat(stat, callSnapshot)))
        .compose(stat -> statRepository.save(stat)));
  }

  private Stat removeCallFromStat(Stat stat, CallSnapshot callSnapshot) {
    stat.totalDurationByCallType(callSnapshot.type()).decreaseDuration(callSnapshot.duration());
    stat.decrementTotalNumberOfCalls();
    var callersCount = stat.numberOfCallsByCaller(callSnapshot.callerNumber());
    callersCount.decrementNumberOfCalls();
    if (callersCount.numberOfCalls() == 0) {
      stat.numberOfCallsByCaller().remove(callSnapshot.callerNumber());
    }
    var calleesCount = stat.numberOfCallsByCallee(callSnapshot.calleeNumber());
    calleesCount.decrementNumberOfCalls();
    if (calleesCount.numberOfCalls() == 0) {
      stat.numberOfCallsByCallee().remove(callSnapshot.calleeNumber());
    }
    stat.decreaseTotalCost(callSnapshot.cost());
    return stat;
  }
}

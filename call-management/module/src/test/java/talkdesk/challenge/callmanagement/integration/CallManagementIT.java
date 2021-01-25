package talkdesk.challenge.callmanagement.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import talkdesk.challenge.callmanagement.CallManagement;
import talkdesk.challenge.callmanagement.api.command.CreateCall;
import talkdesk.challenge.callmanagement.api.command.DeleteCall;
import talkdesk.challenge.callmanagement.api.event.CallCreated;
import talkdesk.challenge.callmanagement.api.event.CallDeleted;
import talkdesk.challenge.callmanagement.api.model.Call;
import talkdesk.challenge.callmanagement.api.model.CallType;
import talkdesk.challenge.callmanagement.api.model.Cost;
import talkdesk.challenge.callmanagement.api.model.Phone;
import talkdesk.challenge.callmanagement.api.query.GetCalls;
import talkdesk.challenge.core.model.Page;
import talkdesk.challenge.core.model.Paginated;
import talkdesk.challenge.core.runtime.Application;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(VertxExtension.class)
@ExtendWith(MockitoExtension.class)
public class CallManagementIT {
  private Application app;
  private CallEventLogger eventLogger;

  @BeforeEach
  public void setUp(Vertx vertx, VertxTestContext context) {
    app = new Application(vertx);
    app.run(programArgs())
      .onSuccess(x -> {
        eventLogger = new CallEventLogger();
        app.eventBus().subscribe("call", eventLogger);
      }).onSuccess(x -> app.deployNode(new CallManagement()))
      .onSuccess(x -> context.completeNow())
      .onFailure(e -> context.failNow(e));
  }

  protected String[] programArgs() {
    return new String[0];
  }

  @Test
  public void callTest(Vertx vertx, VertxTestContext context) {
    var createCallCommand = createCallCommand();
    var expectedCost = Cost.of(5 * 10 + 3 * 5);
    var expectedCall = createExpectedObject(createCallCommand, expectedCost);
    var expectedCallCreatedEvent = createExpectedCallCreatedEvent(createCallCommand, expectedCost);
    var expectedCallDeletedEvent = createExpectedCallDeletedEvent(expectedCall.uuid());
    app.communicationBus().order("call-management.create-call", createCallCommand)
      .onSuccess(x -> context.verify(() -> {
        assertThat(eventLogger.events(), hasItem(equalTo(expectedCallCreatedEvent)));
      }))
      .compose(x -> app.communicationBus().ask("call-management.get-calls", getCallsQuery(), new TypeReference<Paginated<Call>>() {}))
      .map(x -> x.items())
      .onSuccess(calls -> context.verify(() -> {
        assertThat(calls.size(), is(1));
        assertThat(calls, hasItem(expectedCall));
      }))
      .compose(calls -> app.communicationBus().order("call-management.delete-call", deleteCallCommand(calls.stream().findFirst().get().uuid())))
      .onSuccess(x -> context.verify(() -> {
        assertThat(eventLogger.events(), hasItem(equalTo(expectedCallDeletedEvent)));
      }))
      .compose(x -> app.communicationBus().ask("call-management.get-calls", getCallsQuery(), new TypeReference<Paginated<Call>>() {}))
      .map(x -> x.items())
      .onSuccess(calls -> context.verify(() -> {
        assertThat(calls, empty());
      }))
      .onSuccess(x -> context.completeNow())
      .onFailure(e -> context.failNow(e));
  }

  private CreateCall createCallCommand() {
    LocalDateTime now = LocalDateTime.now();
    var command = new CreateCall();
    command.uuid(UUID.randomUUID());
    command.callerNumber(Phone.of("12345"));
    command.calleeNumber(Phone.of("54321"));
    command.startedAt(now.minusMinutes(10));
    command.endedAt(now.minusMinutes(2));
    command.type(CallType.OUTBOUND);
    return command;
  }

  private Call createExpectedObject(CreateCall command, Cost cost) {
    var call = new Call();
    call.uuid(command.uuid());
    call.callerNumber(command.callerNumber());
    call.calleeNumber(command.calleeNumber());
    call.startedAt(command.startedAt());
    call.endedAt(command.endedAt());
    call.type(command.type());
    call.cost(cost);
    return call;
  }

  private CallCreated createExpectedCallCreatedEvent(CreateCall command, Cost cost) {
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

  private CallDeleted createExpectedCallDeletedEvent(UUID uuid) {
    var event = new CallDeleted();
    event.uuid(uuid);
    return event;
  }

  private GetCalls getCallsQuery() {
    var query = new GetCalls();
    query.page(new Page(0, 10));
    return query;
  }

  private DeleteCall deleteCallCommand(UUID uuid) {
    var command = new DeleteCall();
    command.uuid(uuid);
    return command;
  }
}

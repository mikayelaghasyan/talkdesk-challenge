package talkdesk.challenge.callmanagement.integration;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import talkdesk.challenge.callmanagement.CallManagement;
import talkdesk.challenge.callmanagement.command.CreateCall;
import talkdesk.challenge.callmanagement.command.DeleteCall;
import talkdesk.challenge.callmanagement.event.CallCreated;
import talkdesk.challenge.callmanagement.event.CallDeleted;
import talkdesk.challenge.callmanagement.model.Call;
import talkdesk.challenge.callmanagement.model.CallType;
import talkdesk.challenge.callmanagement.model.Cost;
import talkdesk.challenge.callmanagement.model.Phone;
import talkdesk.challenge.callmanagement.query.GetCalls;
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
  public void setUp(Vertx vertx) {
    JsonObject config = createConfig();
    app = new Application(vertx, config);

    eventLogger = new CallEventLogger();
    app.eventBus().subscribe("call", eventLogger);

    CallManagement node = new CallManagement();
    app.deployNode(node);
  }

  @Test
  public void callTest(Vertx vertx, VertxTestContext context) {
    var createCallCommand = createCallCommand();
    var expectedCall = createExpectedObject(createCallCommand);
    var expectedCost = Cost.of(5 * 10 + 3 * 5);
    var expectedCallCreatedEvent = createExpectedCallCreatedEvent(createCallCommand, expectedCost);
    var expectedCallDeletedEvent = createExpectedCallDeletedEvent(expectedCall.uuid());
    app.communicationBus().order("call-management.create-call", createCallCommand())
      .onSuccess(x -> context.verify(() -> {
        assertThat(eventLogger.events(), hasItem(equalTo(expectedCallCreatedEvent)));
      }))
      .compose(x -> app.communicationBus().ask("call-management.get-calls", getCallsQuery(), Call[].class))
      .onSuccess(calls -> context.verify(() -> {
        assertThat(calls.length, is(1));
        assertThat(calls, hasItemInArray(expectedCall));
      }))
      .compose(calls -> app.communicationBus().order("call-management.delete-call", deleteCallCommand(calls[0].uuid())))
      .onSuccess(x -> context.verify(() -> {
        assertThat(eventLogger.events(), hasItem(equalTo(expectedCallDeletedEvent)));
      }))
      .compose(x -> app.communicationBus().ask("call-management.get-calls", getCallsQuery(), Call[].class))
      .onSuccess(calls -> context.verify(() -> {
        assertThat(calls, emptyArray());
      }))
      .onSuccess(x -> context.completeNow())
      .onFailure(e -> context.failNow(e));
  }

  private JsonObject createConfig() {
    JsonObject config = new JsonObject();
    return config;
  }

  private CreateCall createCallCommand() {
    LocalDateTime now = LocalDateTime.now();
    return CreateCall.builder()
      .uuid(UUID.randomUUID())
      .callerNumber(new Phone("12345"))
      .calleeNumber(new Phone("54321"))
      .startedAt(now.minusMinutes(10))
      .endedAt(now.minusMinutes(2))
      .type(CallType.INBOUND)
      .build();
  }

  private Call createExpectedObject(CreateCall command) {
    return Call.builder()
      .uuid(command.uuid())
      .callerNumber(command.callerNumber())
      .calleeNumber(command.calleeNumber())
      .startedAt(command.startedAt())
      .endedAt(command.endedAt())
      .type(command.type())
      .build();
  }

  private CallCreated createExpectedCallCreatedEvent(CreateCall command, Cost cost) {
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

  private CallDeleted createExpectedCallDeletedEvent(UUID uuid) {
    return CallDeleted.builder()
      .uuid(uuid)
      .build();
  }

  private GetCalls getCallsQuery() {
    return GetCalls.builder().build();
  }

  private DeleteCall deleteCallCommand(UUID uuid) {
    return null;
  }
}

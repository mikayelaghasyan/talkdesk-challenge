package talkdesk.challenge.callmanagement.unit;

import io.vertx.core.Future;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import talkdesk.challenge.callmanagement.command.CreateCall;
import talkdesk.challenge.callmanagement.event.CallCreated;
import talkdesk.challenge.callmanagement.handler.CreateCallHandler;
import talkdesk.challenge.callmanagement.model.Call;
import talkdesk.challenge.callmanagement.model.CallType;
import talkdesk.challenge.callmanagement.model.Cost;
import talkdesk.challenge.callmanagement.model.Phone;
import talkdesk.challenge.callmanagement.query.CalculateCost;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(VertxExtension.class)
@ExtendWith(MockitoExtension.class)
public class CreateCallTest extends BaseCallTest {
  private CreateCallHandler handler;

  @BeforeEach
  void setUp() {
    handler = new CreateCallHandler();
  }

  private static Stream<Arguments> provideParams() {
    return Stream.of(
      Arguments.of(CallType.INBOUND, Cost.ofZero()),
      Arguments.of(CallType.OUTBOUND, Cost.of(5 * 10 + 3 * 5))
    );
  }

  @ParameterizedTest
  @MethodSource("provideParams")
  void testCreateCall(CallType type, Cost expectedCost, VertxTestContext context) {
    var command = createCommand(type);
    var expectedObject = createExpectedObject(command, expectedCost);
    var expectedEvent = createExpectedEvent(command, expectedCost);
    stubCostQueryHandler(expectedCost);

    handler.handle(commandContext, command)
      .onSuccess(x -> verify(readWriteCallRepository, times(1)).save(expectedObject))
      .onSuccess(x -> verify(eventBus, times(1)).publish(eq("call"), eq(expectedEvent)))
      .onSuccess(x -> context.completeNow())
      .onFailure(e -> context.failNow(e));
  }

  private CreateCall createCommand(CallType type) {
    LocalDateTime now = LocalDateTime.now();
    var command = new CreateCall();
    command.uuid(UUID.randomUUID());
    command.callerNumber(Phone.of("12345"));
    command.calleeNumber(Phone.of("54321"));
    command.startedAt(now.minusMinutes(10));
    command.endedAt(now.minusMinutes(2));
    command.type(type);
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
    return call;
  }

  private CallCreated createExpectedEvent(CreateCall command, Cost cost) {
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

  private void stubCostQueryHandler(Cost expectedCost) {
    when(communicationBus.ask(eq("call-management.calculate-cost"),
      any(CalculateCost.class), eq(Cost.class)))
      .thenReturn(Future.succeededFuture(expectedCost));
  }
}

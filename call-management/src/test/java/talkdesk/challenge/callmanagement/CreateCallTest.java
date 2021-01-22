package talkdesk.challenge.callmanagement;

import io.vertx.core.Future;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import talkdesk.challenge.callmanagement.command.CreateCall;
import talkdesk.challenge.callmanagement.event.CallCreated;
import talkdesk.challenge.callmanagement.handler.CreateCallHandler;
import talkdesk.challenge.callmanagement.model.Call;
import talkdesk.challenge.callmanagement.model.CallType;
import talkdesk.challenge.callmanagement.model.Cost;
import talkdesk.challenge.callmanagement.model.Phone;
import talkdesk.challenge.callmanagement.query.CalculateCost;
import talkdesk.challenge.core.CommandContext;
import talkdesk.challenge.core.CommunicationBus;
import talkdesk.challenge.core.DomainEventBus;
import talkdesk.challenge.core.ReadWriteRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(VertxExtension.class)
@ExtendWith(MockitoExtension.class)
public class CreateCallTest {
  @Mock
  private DomainEventBus eventBus;

  @Mock
  private CommunicationBus communicationBus;

  @Mock
  private ReadWriteRepository<Call> callRepository;

  private CommandContext commandContext;
  private CreateCallHandler handler;

  @BeforeEach
  void setUp() {
    commandContext = spy(new CommandContext(communicationBus, eventBus));
    stubPublish();
    stubRepository();

    handler = new CreateCallHandler();
  }

  @Test
  void testInboundCall(VertxTestContext context) {
    var command = createCommand(CallType.INBOUND);

    var expectedCost = Cost.ofZero();
    var expectedObject = createExpectedObject(command, expectedCost);
    var expectedEvent = createExpectedEvent(command, expectedCost);
    stubCostQueryHandler(expectedCost);

    handler.handle(commandContext, command)
      .onSuccess(x -> verify(callRepository, times(1)).save(expectedObject))
      .onSuccess(x -> verify(eventBus, times(1)).publish(eq(expectedEvent)))
      .onSuccess(x -> context.completeNow())
      .onFailure(e -> context.failNow(e));
  }

  @Test
  void testOutboundCall(VertxTestContext context) {
    var command = createCommand(CallType.OUTBOUND);

    var expectedCost = Cost.of(5 * 10 + 3 * 5);
    var expectedObject = createExpectedObject(command, expectedCost);
    var expectedEvent = createExpectedEvent(command, expectedCost);
    stubCostQueryHandler(expectedCost);

    handler.handle(commandContext, command)
      .onSuccess(x -> verify(callRepository, times(1)).save(expectedObject))
      .onSuccess(x -> verify(eventBus, times(1)).publish(eq(expectedEvent)))
      .onSuccess(x -> context.completeNow())
      .onFailure(e -> context.failNow(e));
  }

  private CreateCall createCommand(CallType type) {
    LocalDateTime now = LocalDateTime.now();
    return CreateCall.builder()
      .uuid(UUID.randomUUID())
      .callerNumber(new Phone("12345"))
      .calleeNumber(new Phone("54321"))
      .startedAt(now.minusMinutes(10))
      .endedAt(now.minusMinutes(2))
      .type(type)
      .build();
  }

  private Call createExpectedObject(CreateCall command, Cost cost) {
    return Call.builder()
      .uuid(command.uuid())
      .callerNumber(command.callerNumber())
      .calleeNumber(command.calleeNumber())
      .startedAt(command.startedAt())
      .endedAt(command.endedAt())
      .type(command.type())
      .build();
  }

  private CallCreated createExpectedEvent(CreateCall command, Cost cost) {
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

  private void stubCostQueryHandler(Cost expectedCost) {
    when(communicationBus.ask(eq("call-management.calculate-cost"),
      any(CalculateCost.class), eq(Cost.class)))
      .thenReturn(Future.succeededFuture(expectedCost));
  }

  private void stubPublish() {
    when(eventBus.publish(any()))
      .thenAnswer(x -> Future.succeededFuture());
  }

  private void stubRepository() {
    when(commandContext.repositoryOf("call"))
      .thenAnswer(x -> callRepository);
    when(callRepository.save(any()))
      .thenAnswer(x -> Future.succeededFuture());
  }
}

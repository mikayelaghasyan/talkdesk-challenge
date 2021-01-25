package talkdesk.challenge.callstatistics.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import talkdesk.challenge.callmanagement.api.event.CallCreated;
import talkdesk.challenge.callmanagement.api.event.CallDeleted;
import talkdesk.challenge.callmanagement.api.event.CallEvent;
import talkdesk.challenge.callmanagement.api.model.CallType;
import talkdesk.challenge.callmanagement.api.model.Cost;
import talkdesk.challenge.callmanagement.api.model.Phone;
import talkdesk.challenge.callstatistics.CallStatistics;
import talkdesk.challenge.callstatistics.api.model.Stat;
import talkdesk.challenge.callstatistics.api.query.GetStats;
import talkdesk.challenge.core.runtime.Application;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(VertxExtension.class)
@ExtendWith(MockitoExtension.class)
public class CallStatisticsIT {
  private Application app;

  @BeforeEach
  public void setUp(Vertx vertx, VertxTestContext context) {
    app = new Application(vertx);
    app.run(new String[0])
      .onSuccess(x -> app.deployNode(new CallStatistics()))
      .onSuccess(x -> context.completeNow())
      .onFailure(e -> context.failNow(e));
  }

  @Test
  public void statsTest(VertxTestContext context) {
    LocalDateTime today = LocalDate.now().atStartOfDay();
    var event1 = createCallCreatedEvent(Phone.of("12345"), Phone.of("23456"),
      today.minus(5, ChronoUnit.DAYS), 15L, CallType.OUTBOUND);
    var event2 = createCallCreatedEvent(Phone.of("12345"), Phone.of("34567"),
        today.minus(5, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS), 3L, CallType.INBOUND);
    var event3 = createCallCreatedEvent(Phone.of("12345"), Phone.of("34567"),
        today.minus(5, ChronoUnit.DAYS).plus(5, ChronoUnit.HOURS), 20L, CallType.OUTBOUND);
    var event4 = createCallCreatedEvent(Phone.of("34567"), Phone.of("12345"),
        today.minus(2, ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS), 34L, CallType.OUTBOUND);
    var event5 = createCallCreatedEvent(Phone.of("34567"), Phone.of("23456"),
        today.minus(2, ChronoUnit.DAYS).plus(10, ChronoUnit.HOURS), 15L, CallType.INBOUND);
    var event6 = createCallDeletedEvent(event3.uuid());

    var expectedDay1Stats = createExpectedDayStats(
      today.minus(5, ChronoUnit.DAYS).toLocalDate(),
      Duration.ofMinutes(3L), Duration.ofMinutes(15L),
      Map.of(Phone.of("12345"), 2L), Map.of(Phone.of("23456"), 1L, Phone.of("34567"), 1L),
      2L, Cost.of(5 * 10 + 10 * 5));
    var expectedDay2Stats = createExpectedDayStats(
      today.minus(2, ChronoUnit.DAYS).toLocalDate(),
      Duration.ofMinutes(15L), Duration.ofMinutes(34L),
      Map.of(Phone.of("34567"), 2L), Map.of(Phone.of("12345"), 1L, Phone.of("23456"), 1L),
      2L, Cost.of(5 * 10 + 29 * 5));

    publishEvent(event1)
      .compose(x -> publishEvent(event2))
      .compose(x -> publishEvent(event3))
      .compose(x -> publishEvent(event4))
      .compose(x -> publishEvent(event5))
      .compose(x -> publishEvent(event6))
      .compose(x -> app.communicationBus().ask("call-statistics.get-stats", getStatsQuery(), new TypeReference<List<Stat>>() {}))
      .onSuccess(stats -> context.verify(() -> {
        assertThat(stats.size(), is(2));
        assertThat(stats.get(0), equalTo(expectedDay1Stats));
        assertThat(stats.get(1), equalTo(expectedDay2Stats));
      }))
      .onSuccess(x -> context.completeNow())
      .onFailure(e -> context.failNow(e));
  }

  private Stat createExpectedDayStats(LocalDate date, Duration inboundDuration, Duration outboundDuration,
                                      Map<Phone, Long> numberOfCallsByCaller, Map<Phone, Long> numberOfCallsByCallee,
                                      long totalNumberOfCalls, Cost totalCost) {
    var stats = new Stat();
    stats.date(date);
    stats.totalDurationByCallType(CallType.INBOUND).duration(inboundDuration);
    stats.totalDurationByCallType(CallType.OUTBOUND).duration(outboundDuration);
    numberOfCallsByCaller.forEach((phone, count) -> stats.numberOfCallsByCaller(phone).numberOfCalls(count));
    numberOfCallsByCallee.forEach((phone, count) -> stats.numberOfCallsByCallee(phone).numberOfCalls(count));
    stats.totalNumberOfCalls(totalNumberOfCalls);
    stats.totalCost(totalCost);
    return stats;
  }

  private CallCreated createCallCreatedEvent(Phone callerNumber, Phone calleeNumber, LocalDateTime startedAt, long durationMinutes, CallType type) {
    var event = new CallCreated();
    event.uuid(UUID.randomUUID());
    event.callerNumber(callerNumber);
    event.calleeNumber(calleeNumber);
    event.startedAt(startedAt);
    event.endedAt(startedAt.plus(durationMinutes, ChronoUnit.MINUTES));
    event.type(type);
    if (type == CallType.INBOUND) {
      event.cost(Cost.ofZero());
    } else {
      event.cost(Cost.of(Math.min(durationMinutes, 5) * 10 + Math.max(durationMinutes - 5, 0) * 5));
    }
    return event;
  }

  private CallDeleted createCallDeletedEvent(UUID uuid) {
    var event = new CallDeleted();
    event.uuid(uuid);
    return event;
  }

  private GetStats getStatsQuery() {
    return new GetStats();
  }

  private Future<Void> publishEvent(CallEvent event) {
    return app.eventBus().publish("call", event);
  }
}

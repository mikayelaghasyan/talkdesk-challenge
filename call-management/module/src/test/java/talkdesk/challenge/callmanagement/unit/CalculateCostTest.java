package talkdesk.challenge.callmanagement.unit;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import talkdesk.challenge.callmanagement.handler.CalculateCostHandler;
import talkdesk.challenge.callmanagement.api.model.CallType;
import talkdesk.challenge.callmanagement.api.model.Cost;
import talkdesk.challenge.callmanagement.api.query.CalculateCost;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(VertxExtension.class)
@ExtendWith(MockitoExtension.class)
public class CalculateCostTest extends BaseCallTest {
  private CalculateCostHandler handler;

  @BeforeEach
  void setUp() {
    JsonObject tariff = new JsonObject()
      .put("inboundRules", new JsonArray()
        .add(new JsonObject().put("upTo", null).put("cost", 0)))
      .put("outboundRules", new JsonArray()
        .add(new JsonObject().put("upTo", 5).put("cost", 10))
        .add(new JsonObject().put("upTo", null).put("cost", 5)));
    handler = new CalculateCostHandler(tariff);
  }


  private static Stream<Arguments> provideParams() {
    LocalDateTime now = LocalDateTime.now();
    return Stream.of(
      Arguments.of(now, now, CallType.INBOUND, Cost.ofZero()),
      Arguments.of(now.minusMinutes(5), now, CallType.INBOUND, Cost.ofZero()),
      Arguments.of(now.minusMinutes(10), now, CallType.INBOUND, Cost.ofZero()),
      Arguments.of(now, now, CallType.OUTBOUND, Cost.ofZero()),
      Arguments.of(now.minusMinutes(4), now, CallType.OUTBOUND, Cost.of(4 * 10)),
      Arguments.of(now.minusMinutes(5), now, CallType.OUTBOUND, Cost.of(5 * 10)),
      Arguments.of(now.minusMinutes(6), now, CallType.OUTBOUND, Cost.of(5 * 10 + 5)),
      Arguments.of(now.minusMinutes(10), now, CallType.OUTBOUND, Cost.of(5 * 10 + 5 * 5))
    );
  }

  @ParameterizedTest
  @MethodSource("provideParams")
  void testCalculateCost(LocalDateTime start, LocalDateTime end, CallType type, Cost expectedCost, VertxTestContext context) {
    var query = createQuery(start, end, type);

    handler.handle(queryContext, query)
      .onSuccess(cost -> context.verify(() -> assertThat(cost, equalTo(expectedCost))))
      .onSuccess(x -> context.completeNow())
      .onFailure(context::failNow);
  }

  private CalculateCost createQuery(LocalDateTime start, LocalDateTime end, CallType type) {
    var query = new CalculateCost();
    query.startedAt(start);
    query.endedAt(end);
    query.type(type);
    return query;
  }
}

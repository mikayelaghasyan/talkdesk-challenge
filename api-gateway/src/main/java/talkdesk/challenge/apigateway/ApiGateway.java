package talkdesk.challenge.apigateway;

import com.fasterxml.jackson.core.type.TypeReference;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.json.schema.SchemaParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import talkdesk.challenge.apigateway.validation.DependencyValidatorFactory;
import talkdesk.challenge.callmanagement.api.command.CreateCall;
import talkdesk.challenge.callmanagement.api.command.DeleteCall;
import talkdesk.challenge.callmanagement.api.model.Call;
import talkdesk.challenge.callmanagement.api.model.CallType;
import talkdesk.challenge.callmanagement.api.model.Phone;
import talkdesk.challenge.callmanagement.api.query.GetCalls;
import talkdesk.challenge.callstatistics.api.model.Stat;
import talkdesk.challenge.callstatistics.api.query.GetStats;
import talkdesk.challenge.core.error.NotFound;
import talkdesk.challenge.core.model.Order;
import talkdesk.challenge.core.model.Page;
import talkdesk.challenge.core.model.Paginated;
import talkdesk.challenge.core.runtime.Node;
import talkdesk.challenge.core.runtime.RuntimeContext;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ApiGateway extends Node {
  private static final Logger log = LoggerFactory.getLogger(ApiGateway.class);

  @Override
  protected Future<Void> run(RuntimeContext context) {
    try {
      File file = new File(context.config().getString("specPath"));
      int httpPort = Optional.ofNullable(context.config().getInteger("httpPort")).orElse(8080);

      return RouterBuilder.create(vertx, file.getCanonicalPath())
        .onSuccess(x -> configureSchemaParser(x.getSchemaParser()))
        .onSuccess(x -> configureHandlers(context, x))
        .onFailure(e -> log.error(e.getLocalizedMessage()))
        .map(x -> x.rootHandler(LoggerHandler.create()))
        .map(RouterBuilder::createRouter)
        .compose(apiRouter -> vertx.createHttpServer().requestHandler(apiRouter).listen(httpPort))
        .onSuccess(x -> log.info("HTTP server started on port {}", x.actualPort()))
        .onFailure(x -> log.info("HTTP server cannot be started: {}", x.getLocalizedMessage()))
        .map(x -> null);
    } catch (IOException e) {
      return Future.failedFuture(e);
    }
  }

  private void configureSchemaParser(SchemaParser schemaParser) {
    schemaParser.withValidatorFactory(new DependencyValidatorFactory());
  }

  private void configureHandlers(RuntimeContext runtimeContext, RouterBuilder builder) {
    builder.operation("AddCall").handler(context -> {
      var body = context.getBodyAsJson();
      createCall(runtimeContext, body)
        .onSuccess(result -> createdResponse(context, convertToViewModel(result)));
    });
    builder.operation("GetCalls").handler(context -> {
      var callType = context.queryParam("type").stream()
        .findFirst().map(CallType::valueOf).orElse(null);
      var page = new Page(
        context.queryParam("page_num").stream().findFirst().map(Long::parseLong).orElse(0L),
        context.queryParam("page_size").stream().findFirst().map(Long::parseLong).orElse(10L)
      );
      var order = context.queryParam("sort_by").stream()
        .findFirst().map(field -> new Order(field,
          context.queryParam("sort_dir").stream()
            .findFirst().map(Order.Direction::valueOf)
            .orElse(Order.Direction.ASC)))
        .orElse(null);
      getCalls(runtimeContext, callType, page, order)
        .onSuccess(result -> successResponse(context, convertToViewModel(result, this::convertToViewModel)));
    });
    builder.operation("RemoveCall").handler(context -> {
      UUID uuid = UUID.fromString(context.pathParam("id"));
      deleteCall(runtimeContext, uuid)
        .onSuccess(x -> deletedResponse(context))
        .onFailure(t -> failureResponse(context, t));
    });
    builder.operation("AddCalls").handler(context -> {
      var body = context.getBodyAsJsonArray();

      var futureList = body.stream()
        .map(x -> createCall(runtimeContext, (JsonObject) x))
        .collect(Collectors.toList());

      CompositeFuture.all(new ArrayList<>(futureList))
        .map(x -> futureList.stream()
          .map(Future::result).collect(Collectors.toList()))
        .onSuccess(result -> createdResponse(context, convertToViewModel(result, this::convertToViewModel)));
    });
    builder.operation("GetStats").handler(context -> getStats(runtimeContext)
      .onSuccess(result -> successResponse(context, convertToViewModel(result, this::convertToViewModel))));
  }

  private Future<Call> createCall(RuntimeContext runtimeContext, JsonObject obj) {
    var uuid = UUID.randomUUID();
    var command = new CreateCall();
    command.uuid(uuid);
    command.callerNumber(Phone.of(obj.getString("callerNumber")));
    command.calleeNumber(Phone.of(obj.getString("calleeNumber")));
    command.startedAt(LocalDateTime.from(OffsetDateTime.parse(obj.getString("start"))));
    command.endedAt(LocalDateTime.from(OffsetDateTime.parse(obj.getString("end"))));
    command.type(CallType.valueOf(obj.getString("type")));
    return runtimeContext.communicationBus().order("call-management.create-call", command)
      .compose(x -> runtimeContext.communicationBus().ask("call-management.get-call", uuid, Call.class));
  }

  private Future<Void> deleteCall(RuntimeContext runtimeContext, UUID uuid) {
    var command = new DeleteCall();
    command.uuid(uuid);
    return runtimeContext.communicationBus().order("call-management.delete-call", command);
  }

  private Future<Paginated<Call>> getCalls(RuntimeContext runtimeContext, CallType callType, Page page, Order order) {
    var query = new GetCalls();
    query.type(callType);
    query.page(page);
    query.order(order);
    return runtimeContext.communicationBus()
      .ask("call-management.get-calls", query, new TypeReference<>() {});
  }

  private Future<List<Stat>> getStats(RuntimeContext runtimeContext) {
    var query = new GetStats();
    return runtimeContext.communicationBus()
      .ask("call-statistics.get-stats", query, new TypeReference<>() {});
  }

  private JsonObject convertToViewModel(Call call) {
    return new JsonObject()
      .put("id", call.uuid())
      .put("callerNumber", call.callerNumber().toString())
      .put("calleeNumber", call.calleeNumber().toString())
      .put("start", call.startedAt())
      .put("end", call.endedAt())
      .put("type", call.type())
      .put("cost", Double.parseDouble(call.cost().toString()));
  }

  private JsonObject convertToViewModel(Stat stat) {
    return new JsonObject()
      .put("date", stat.date())
      .put("totalDurationByCallType", new JsonObject()
        .put(CallType.INBOUND.toString(), stat.totalDurationByCallType(CallType.INBOUND).duration().getSeconds())
        .put(CallType.OUTBOUND.toString(), stat.totalDurationByCallType(CallType.OUTBOUND).duration().getSeconds()))
      .put("totalNumberOfCalls", stat.totalNumberOfCalls())
      .put("numberOfCallsByCaller", stat.numberOfCallsByCaller().keySet().stream()
        .reduce(new JsonArray(), (arr, key) ->
          arr.add(new JsonObject()
            .put("phone", key.toString())
            .put("numberOfCalls", stat.numberOfCallsByCaller(key).numberOfCalls())), (a, b) -> b))
      .put("numberOfCallsByCallee", stat.numberOfCallsByCallee().keySet().stream()
        .reduce(new JsonArray(), (arr, key) ->
          arr.add(new JsonObject()
            .put("phone", key.toString())
            .put("numberOfCalls", stat.numberOfCallsByCallee(key).numberOfCalls())), (a, b) -> b))
      .put("totalCost", Double.parseDouble(stat.totalCost().toString()));
  }

  private <U> JsonObject convertToViewModel(Paginated<U> paginated, Function<U, JsonObject> itemConverter) {
    return new JsonObject()
      .put("pageNumber", paginated.page().number())
      .put("pageSize", paginated.page().size())
      .put("totalCount", paginated.totalCount())
      .put("items", convertToViewModel(paginated.items(), itemConverter));
  }

  private <U> JsonArray convertToViewModel(List<U> list, Function<U, JsonObject> itemConverter) {
    return list.stream().reduce(new JsonArray(),
      (arr, item) -> arr.add(itemConverter.apply(item)),
        (a, b) -> b);
  }

  private void createdResponse(RoutingContext context, Object result) {
    context.response()
      .setStatusCode(201)
      .setStatusMessage("Created")
      .end(Json.encodeToBuffer(result));
  }

  private void deletedResponse(RoutingContext context) {
    context.response()
      .setStatusCode(204)
      .setStatusMessage("Deleted")
      .end();
  }

  private void successResponse(RoutingContext context, Object result) {
    context.response()
      .setStatusCode(200)
      .setStatusMessage("Success")
      .end(Json.encodeToBuffer(result));
  }

  private void failureResponse(RoutingContext context, Throwable t) {
    if (t instanceof ReplyException) {
      int code = ((ReplyException)t).failureCode();
      if (NotFound.CODE == code) {
        context.response()
          .setStatusCode(404)
          .setStatusMessage("Not found")
          .end();
        return;
      }
    }
    context.response()
      .setStatusCode(500)
      .setStatusMessage("Internal server error")
      .end();
  }
}

package talkdesk.challenge.core.vertx;

import com.fasterxml.jackson.core.type.TypeReference;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import talkdesk.challenge.core.communication.*;
import talkdesk.challenge.core.error.Error;
import talkdesk.challenge.core.runtime.ApplicationContext;

public class VertxCommunicationBus extends VertxBus implements CommunicationBus {
  private static final Logger log = LoggerFactory.getLogger(VertxCommunicationBus.class);

  private final ApplicationContext applicationContext;

  public VertxCommunicationBus(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public <U, V> Future<V> ask(String address, U query, Class<V> resultClass) {
    log.debug("sent: ({}, {})", address, query);
    return applicationContext.vertx().eventBus().<Buffer>request(address, Json.encodeToBuffer(query), optionsFor(query))
      .map(Message::body)
      .map(result -> Json.decodeValue(result, resultClass))
      .onSuccess(reply -> log.debug("received: ({}, {})", address, reply))
      .onFailure(error -> log.debug("failed: ({}, {})", address, error.getLocalizedMessage()));
  }

  public <U, V> Future<V> ask(String address, U query, TypeReference<V> resultTypeRef) {
    log.debug("sent: ({}, {})", address, query);
    return applicationContext.vertx().eventBus().<Buffer>request(address, Json.encodeToBuffer(query), optionsFor(query))
      .map(Message::body)
      .map(result -> ((DatabindCodec)Json.CODEC).fromBuffer(result, resultTypeRef))
      .onSuccess(reply -> log.debug("received: ({}, {})", address, reply))
      .onFailure(error -> log.debug("failed: ({}, {})", address, error.getLocalizedMessage()));
  }

  @Override
  public <U> Future<Void> order(String address, U command) {
    log.debug("sent: ({}, {})", address, command);
    return applicationContext.vertx().eventBus().<Buffer>request(address, Json.encodeToBuffer(command), optionsFor(command))
      .onSuccess(x -> log.debug("received: ({}, {})", address, null))
      .onFailure(error -> log.debug("failed: ({}, {})", address, error.getLocalizedMessage()))
      .map(x -> null);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <U, V> void replyTo(String address, QueryHandler<U, V> handler) {
    log.debug("subscribed: {}", address);
    applicationContext.vertx().eventBus().<Buffer>consumer(address, message ->
      Future.succeededFuture(message.body())
        .map(body -> Json.decodeValue(body, classFromHeader(message.headers()).orElse(JsonObject.class)))
        .onSuccess(query -> log.debug("received: ({}, {})", address, query))
        .onFailure(error -> log.debug("failed: ({}, {})", address, error.getLocalizedMessage()))
        .compose(query -> handler.handle(createQueryContext(), (U)query))
        .onSuccess(reply -> message.reply(Json.encodeToBuffer(reply), optionsFor(reply)))
        .onFailure(error -> {
          int code = (error instanceof Error) ? ((Error)error).code() : 0;
          message.fail(code, error.getLocalizedMessage());
        })
        .onSuccess(reply -> log.debug("sent: ({}, {})", address, reply))
        .onFailure(error -> log.debug("failed: ({}, {})", address, error.getLocalizedMessage())));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <U> void obeyTo(String address, CommandHandler<U> handler) {
    log.debug("subscribed: {}", address);
    applicationContext.vertx().eventBus().<Buffer>consumer(address, message ->
      Future.succeededFuture(message.body())
        .map(body -> Json.decodeValue(body, classFromHeader(message.headers()).orElse(JsonObject.class)))
        .onSuccess(command -> log.debug("received: ({}, {})", address, command))
        .onFailure(error -> log.debug("failed: ({}, {})", address, error.getLocalizedMessage()))
        .compose(command -> handler.handle(createCommandContext(), (U)command))
        .onSuccess(x -> message.reply(null))
        .onFailure(error -> {
          int code = (error instanceof Error) ? ((Error)error).code() : 0;
          message.fail(code, error.getLocalizedMessage());
        })
        .onSuccess(x -> log.debug("sent: ({}, {})", address, null))
        .onFailure(error -> log.debug("failed: ({}, {})", address, error.getLocalizedMessage())));
  }

  private QueryContext createQueryContext() {
    return new QueryContext(applicationContext.communicationBus(), applicationContext.dbGateway());
  }

  private CommandContext createCommandContext() {
    return new CommandContext(applicationContext.communicationBus(), applicationContext.eventBus(), applicationContext.dbGateway());
  }
}

package talkdesk.challenge.core.communication.vertx;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import talkdesk.challenge.core.communication.*;
import talkdesk.challenge.core.runtime.ApplicationContext;

public class VertxCommunicationBus implements CommunicationBus {
  private static final Logger log = LoggerFactory.getLogger(VertxCommunicationBus.class);

  private final ApplicationContext applicationContext;

  public VertxCommunicationBus(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public <U, V> Future<V> ask(String address, U query, Class<V> resultClass) {
    log.debug("sent: ({}, {})", address, query);
    return applicationContext.vertx().eventBus().<Buffer>request(address, Json.encodeToBuffer(query))
      .map(reply -> reply.body())
      .map(result -> Json.decodeValue(result, resultClass))
      .onSuccess(reply -> log.debug("received: ({}, {})", address, reply))
      .onFailure(error -> log.debug("failed: ({}, {})", address, error.getLocalizedMessage()));
  }

  @Override
  public <U> Future<Void> order(String address, U command) {
    log.debug("sent: ({}, {})", address, command);
    return applicationContext.vertx().eventBus().<Buffer>request(address, Json.encodeToBuffer(command))
      .onSuccess(x -> log.debug("received: ({}, {})", address, null))
      .onFailure(error -> log.debug("failed: ({}, {})", address, error.getLocalizedMessage()))
      .map(x -> null);
  }

  @Override
  public <U, V> void replyTo(String address, QueryHandler<U, V> handler) {
    log.debug("subscribed: {}", address);
    applicationContext.vertx().eventBus().<Buffer>consumer(address, message ->
      Future.succeededFuture(message.body())
        .map(body -> Json.decodeValue(body, handler.inputClass()))
        .onSuccess(query -> log.debug("received: ({}, {})", address, query))
        .onFailure(error -> log.debug("failed: ({}, {})", address, error.getLocalizedMessage()))
        .compose(query -> handler.handle(createQueryContext(), query))
        .map(reply -> Json.encodeToBuffer(reply))
        .onSuccess(buffer -> message.reply(buffer))
        .onFailure(error -> message.fail(0, error.getLocalizedMessage()))
        .onSuccess(reply -> log.debug("sent: ({}, {})", address, reply))
        .onFailure(error -> log.debug("failed: ({}, {})", address, error.getLocalizedMessage())));
  }

  @Override
  public <U> void obeyTo(String address, CommandHandler<U> handler) {
    log.debug("subscribed: {}", address);
    applicationContext.vertx().eventBus().<Buffer>consumer(address, message ->
      Future.succeededFuture(message.body())
        .map(body -> Json.decodeValue(body, handler.inputClass()))
        .onSuccess(command -> log.debug("received: ({}, {})", address, command))
        .onFailure(error -> log.debug("failed: ({}, {})", address, error.getLocalizedMessage()))
        .compose(command -> handler.handle(createCommandContext(), command))
        .onSuccess(x -> message.reply(null))
        .onFailure(error -> message.fail(0, error.getLocalizedMessage()))
        .onSuccess(x -> log.debug("sent: ({}, {})", address, null))
        .onFailure(error -> log.debug("failed: ({}, {})", address, error.getLocalizedMessage())));
  }

  private QueryContext createQueryContext() {
    return new QueryContext(applicationContext.communicationBus());
  }

  private CommandContext createCommandContext() {
    return new CommandContext(applicationContext.communicationBus(), applicationContext.eventBus());
  }
}

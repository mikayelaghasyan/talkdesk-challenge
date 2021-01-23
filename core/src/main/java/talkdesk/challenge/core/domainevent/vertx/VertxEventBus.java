package talkdesk.challenge.core.domainevent.vertx;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import talkdesk.challenge.core.domainevent.DomainEvent;
import talkdesk.challenge.core.domainevent.DomainEventBus;
import talkdesk.challenge.core.domainevent.EventHandler;
import talkdesk.challenge.core.runtime.ApplicationContext;

public class VertxEventBus implements DomainEventBus {
  private final ApplicationContext applicationContext;

  public VertxEventBus(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public Future<Void> publish(DomainEvent event) {
    return null;
  }

  @Override
  public <U> void subscribe(String address, EventHandler<U> handler) {

  }
}

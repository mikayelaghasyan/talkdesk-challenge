package talkdesk.challenge.callmanagement.api.event;

import talkdesk.challenge.core.domainevent.DomainEvent;

import java.util.Objects;
import java.util.UUID;

public abstract class CallEvent implements DomainEvent {
  private UUID uuid;

  public CallEvent() {
  }

  @Override
  public UUID uuid() {
    return uuid;
  }

  public void uuid(UUID uuid) {
    this.uuid = uuid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CallEvent that = (CallEvent) o;
    return uuid.equals(that.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }
}

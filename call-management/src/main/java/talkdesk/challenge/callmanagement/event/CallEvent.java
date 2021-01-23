package talkdesk.challenge.callmanagement.event;

import talkdesk.challenge.core.domainevent.DomainEvent;

import java.util.Objects;
import java.util.UUID;

public abstract class CallEvent implements DomainEvent {
  protected UUID uuid;

  public UUID uuid() {
    return uuid;
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

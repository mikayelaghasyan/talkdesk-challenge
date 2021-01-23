package talkdesk.challenge.callmanagement.event;

import talkdesk.challenge.callmanagement.model.CallType;
import talkdesk.challenge.callmanagement.model.Cost;
import talkdesk.challenge.callmanagement.model.Phone;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class CallCreated extends CallEvent {
  private Phone callerNumber;
  private Phone calleeNumber;
  private LocalDateTime startedAt;
  private LocalDateTime endedAt;
  private CallType type;
  private Cost cost;

  private CallCreated() {
  }

  public Phone callerNumber() {
    return callerNumber;
  }

  public Phone calleeNumber() {
    return calleeNumber;
  }

  public LocalDateTime startedAt() {
    return startedAt;
  }

  public LocalDateTime endedAt() {
    return endedAt;
  }

  public CallType type() {
    return type;
  }

  public Cost cost() {
    return cost;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CallCreated that = (CallCreated) o;
    return callerNumber.equals(that.callerNumber) && calleeNumber.equals(that.calleeNumber) && startedAt.equals(that.startedAt) && endedAt.equals(that.endedAt) && type == that.type && cost.equals(that.cost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), callerNumber, calleeNumber, startedAt, endedAt, type, cost);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private UUID uuid;
    private Phone callerNumber;
    private Phone calleeNumber;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private CallType type;
    private Cost cost;

    private Builder() {
    }

    public CallCreated build() {
      var event = new CallCreated();
      event.uuid = uuid;
      event.callerNumber = callerNumber;
      event.calleeNumber = calleeNumber;
      event.startedAt = startedAt;
      event.endedAt = endedAt;
      event.type = type;
      event.cost = cost;
      return event;
    }

    public Builder uuid(UUID uuid) {
      this.uuid = uuid;
      return this;
    }

    public Builder callerNumber(Phone phone) {
      this.callerNumber = phone;
      return this;
    }

    public Builder calleeNumber(Phone phone) {
      this.calleeNumber = phone;
      return this;
    }

    public Builder startedAt(LocalDateTime dt) {
      this.startedAt = dt;
      return this;
    }

    public Builder endedAt(LocalDateTime dt) {
      this.endedAt = dt;
      return this;
    }

    public Builder type(CallType type) {
      this.type = type;
      return this;
    }

    public Builder cost(Cost cost) {
      this.cost = cost;
      return this;
    }
  }
}

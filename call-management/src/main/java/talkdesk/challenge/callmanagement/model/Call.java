package talkdesk.challenge.callmanagement.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Call {
  private UUID uuid;
  private Phone callerNumber;
  private Phone calleeNumber;
  private LocalDateTime startedAt;
  private LocalDateTime endedAt;
  private CallType type;

  private Call() {
  }

  public UUID uuid() {
    return uuid;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Call that = (Call) o;
    return uuid.equals(that.uuid) && callerNumber.equals(that.callerNumber) && calleeNumber.equals(that.calleeNumber) && startedAt.equals(that.startedAt) && endedAt.equals(that.endedAt) && type == that.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, callerNumber, calleeNumber, startedAt, endedAt, type);
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

    private Builder() {
    }

    public Call build() {
      var object = new Call();
      object.uuid = uuid;
      object.callerNumber = callerNumber;
      object.calleeNumber = calleeNumber;
      object.startedAt = startedAt;
      object.endedAt = endedAt;
      object.type = type;
      return object;
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
  }
}

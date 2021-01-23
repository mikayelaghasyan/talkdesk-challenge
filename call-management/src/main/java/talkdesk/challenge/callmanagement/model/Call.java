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

  public Call() {
  }

  public UUID uuid() {
    return uuid;
  }

  public void uuid(UUID uuid) {
    this.uuid = uuid;
  }

  public Phone callerNumber() {
    return callerNumber;
  }

  public void callerNumber(Phone callerNumber) {
    this.callerNumber = callerNumber;
  }

  public Phone calleeNumber() {
    return calleeNumber;
  }

  public void calleeNumber(Phone calleeNumber) {
    this.calleeNumber = calleeNumber;
  }

  public LocalDateTime startedAt() {
    return startedAt;
  }

  public void startedAt(LocalDateTime startedAt) {
    this.startedAt = startedAt;
  }

  public LocalDateTime endedAt() {
    return endedAt;
  }

  public void endedAt(LocalDateTime endedAt) {
    this.endedAt = endedAt;
  }

  public CallType type() {
    return type;
  }

  public void type(CallType type) {
    this.type = type;
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
}

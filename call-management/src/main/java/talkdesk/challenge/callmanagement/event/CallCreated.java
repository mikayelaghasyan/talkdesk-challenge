package talkdesk.challenge.callmanagement.event;

import talkdesk.challenge.callmanagement.model.CallType;
import talkdesk.challenge.callmanagement.model.Cost;
import talkdesk.challenge.callmanagement.model.Phone;

import java.time.LocalDateTime;
import java.util.Objects;

public class CallCreated extends CallEvent {
  private Phone callerNumber;
  private Phone calleeNumber;
  private LocalDateTime startedAt;
  private LocalDateTime endedAt;
  private CallType type;
  private Cost cost;

  public CallCreated() {
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

  public Cost cost() {
    return cost;
  }

  public void cost(Cost cost) {
    this.cost = cost;
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
}

package talkdesk.challenge.callmanagement.api.command;

import talkdesk.challenge.callmanagement.api.model.CallType;
import talkdesk.challenge.callmanagement.api.model.Phone;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreateCall {
  private UUID uuid;
  private Phone callerNumber;
  private Phone calleeNumber;
  private LocalDateTime startedAt;
  private LocalDateTime endedAt;
  private CallType type;

  public CreateCall() {
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
}

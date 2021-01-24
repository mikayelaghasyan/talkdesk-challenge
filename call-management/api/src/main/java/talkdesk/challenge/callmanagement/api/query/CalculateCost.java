package talkdesk.challenge.callmanagement.api.query;

import talkdesk.challenge.callmanagement.api.model.CallType;

import java.time.LocalDateTime;

public class CalculateCost {
  private LocalDateTime startedAt;
  private LocalDateTime endedAt;
  private CallType type;

  public CalculateCost() {
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

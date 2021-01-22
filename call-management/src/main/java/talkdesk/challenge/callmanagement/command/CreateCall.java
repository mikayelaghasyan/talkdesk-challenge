package talkdesk.challenge.callmanagement.command;

import talkdesk.challenge.callmanagement.model.CallType;
import talkdesk.challenge.callmanagement.model.Phone;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreateCall {
  private UUID uuid;
  private Phone callerNumber;
  private Phone calleeNumber;
  private LocalDateTime startedAt;
  private LocalDateTime endedAt;
  private CallType type;

  private CreateCall() {
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

    public CreateCall build() {
      var command = new CreateCall();
      command.uuid = uuid;
      command.callerNumber = callerNumber;
      command.calleeNumber = calleeNumber;
      command.startedAt = startedAt;
      command.endedAt = endedAt;
      command.type = type;
      return command;
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

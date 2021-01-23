package talkdesk.challenge.callmanagement.event;

import java.util.UUID;

public class CallDeleted extends CallEvent {

  private CallDeleted() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private UUID uuid;

    private Builder() {
    }

    public CallDeleted build() {
      var event = new CallDeleted();
      event.uuid = uuid;
      return event;
    }

    public Builder uuid(UUID uuid) {
      this.uuid = uuid;
      return this;
    }
  }
}

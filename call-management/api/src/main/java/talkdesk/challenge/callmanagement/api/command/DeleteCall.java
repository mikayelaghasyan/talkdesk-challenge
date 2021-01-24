package talkdesk.challenge.callmanagement.api.command;

import java.util.UUID;

public class DeleteCall {
  private UUID uuid;

  public DeleteCall() {
  }

  public UUID uuid() {
    return uuid;
  }

  public void uuid(UUID uuid) {
    this.uuid = uuid;
  }
}

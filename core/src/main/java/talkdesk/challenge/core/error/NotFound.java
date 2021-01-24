package talkdesk.challenge.core.error;

import java.util.UUID;

public class NotFound extends Error {
  public NotFound(UUID uuid) {
    super(String.format("Item not found: %s", uuid));
  }
}

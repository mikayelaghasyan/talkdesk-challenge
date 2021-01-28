package talkdesk.challenge.core.error;

import java.util.UUID;

public class NotFound extends Error {
  public static int CODE = 1;
  public NotFound(String message) {
    super(CODE, message);
  }

  public NotFound(UUID uuid) {
    super(CODE, String.format("Item not found: %s", uuid));
  }
}

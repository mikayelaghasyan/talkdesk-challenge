package talkdesk.challenge.core.error;

public class Error extends RuntimeException {
  private final int code;
  public Error(int code, String message) {
    super(message);
    this.code = code;
  }

  public int code() {
    return code;
  }
}

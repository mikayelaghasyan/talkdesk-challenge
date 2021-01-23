package talkdesk.challenge.core.communication;

public abstract class RequestHandler<U, V> {
  private final Class<U> inputClass;

  public RequestHandler(Class<U> inputClass) {
    this.inputClass = inputClass;
  }

  public Class<U> inputClass() {
    return inputClass;
  }
}

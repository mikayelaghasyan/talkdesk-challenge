package talkdesk.challenge.callmanagement.query;

public class GetCalls {
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    public GetCalls build() {
      var query = new GetCalls();
      return query;
    }
  }
}

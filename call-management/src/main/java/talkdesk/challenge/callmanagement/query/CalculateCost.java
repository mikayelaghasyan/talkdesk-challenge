package talkdesk.challenge.callmanagement.query;

public class CalculateCost {
  private CalculateCost() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    public CalculateCost build() {
      var query = new CalculateCost();
      return query;
    }
  }
}

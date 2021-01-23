package talkdesk.challenge.callmanagement.model;

public class Cost {
  private long longValue;

  public Cost() {
  }

  public long longValue() {
    return longValue;
  }

  public void longValue(long longValue) {
    this.longValue = longValue;
  }

  private Cost(long cost) {
    this.longValue = cost;
  }

  public static Cost of(long cost) {
    return new Cost(cost);
  }

  public static Cost ofZero() {
    return of(0);
  }

  public double value() {
    return longValue / 100.0;
  }

  @Override
  public String toString() {
    return String.format("%.2f", value());
  }
}

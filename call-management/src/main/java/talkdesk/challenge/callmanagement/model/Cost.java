package talkdesk.challenge.callmanagement.model;

import java.util.Objects;

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

  public Cost add(Cost other) {
    return Cost.of(this.longValue + other.longValue());
  }

  public Cost multiply(Long multiplier) {
    return Cost.of(this.longValue * multiplier);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Cost cost = (Cost) o;
    return longValue == cost.longValue;
  }

  @Override
  public int hashCode() {
    return Objects.hash(longValue);
  }

  @Override
  public String toString() {
    return String.format("%.2f", value());
  }
}

package talkdesk.challenge.callmanagement.api.model;

import java.util.Objects;

public class Cost implements Comparable<Cost> {
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

  public Cost remove(Cost other) {
    if (this.longValue < other.longValue()) {
      throw new IllegalStateException("Cost can't be negative");
    }
    return Cost.of(this.longValue - other.longValue());
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

  @Override
  public int compareTo(Cost other) {
    return (int)(longValue - other.longValue());
  }
}

package talkdesk.challenge.callmanagement.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import talkdesk.challenge.callmanagement.api.serdes.CostDeserializer;
import talkdesk.challenge.callmanagement.api.serdes.CostSerializer;

import java.util.Objects;

@JsonSerialize(using = CostSerializer.class)
@JsonDeserialize(using = CostDeserializer.class)
public class Cost implements Comparable<Cost> {
  private long longValue;

  public Cost() {
  }

  public long longValue() {
    return longValue;
  }

  public void longValue(long longValue) {
    this.longValue = validated(longValue);
  }

  private Cost(long cost) {
    this.longValue = validated(cost);
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
    return Cost.of(this.longValue - other.longValue());
  }

  public Cost multiply(Long multiplier) {
    return Cost.of(this.longValue * multiplier);
  }

  private long validated(long value) {
    if (value < 0) {
      throw new IllegalStateException("Cost can't be negative");
    }
    return value;
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

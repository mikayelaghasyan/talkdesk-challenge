package talkdesk.challenge.core.model;

import java.util.Objects;

public class Order {
  private String field;
  private Direction direction;

  public Order() {
  }

  public Order(String field, Direction direction) {
    this.field = field;
    this.direction = direction;
  }

  public String field() {
    return field;
  }

  public void field(String field) {
    this.field = field;
  }

  public Direction direction() {
    return direction;
  }

  public void direction(Direction direction) {
    this.direction = direction;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Order order = (Order) o;
    return field.equals(order.field) && direction == order.direction;
  }

  @Override
  public int hashCode() {
    return Objects.hash(field, direction);
  }

  public enum Direction {
    ASC, DESC
  }
}

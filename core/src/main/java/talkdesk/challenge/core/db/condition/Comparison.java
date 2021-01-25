package talkdesk.challenge.core.db.condition;

public abstract class Comparison implements Condition {
  public String field;
  public Object value;

  public Comparison(String field, Object value) {
    this.field = field;
    this.value = value;
  }
}

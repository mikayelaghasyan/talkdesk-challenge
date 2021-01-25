package talkdesk.challenge.core.db.condition;

public abstract class BinaryOperator implements Condition {
  public Condition left;
  public Condition right;

  public BinaryOperator(Condition left, Condition right) {
    this.left = left;
    this.right = right;
  }
}

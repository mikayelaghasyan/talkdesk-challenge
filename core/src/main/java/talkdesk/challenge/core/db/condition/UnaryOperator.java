package talkdesk.challenge.core.db.condition;

public abstract class UnaryOperator implements Condition {
  public Condition operand;

  public UnaryOperator(Condition operand) {
    this.operand = operand;
  }
}

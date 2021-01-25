package talkdesk.challenge.core.db.condition;

public class And extends BinaryOperator {
  public And(Condition left, Condition right) {
    super(left, right);
  }
}

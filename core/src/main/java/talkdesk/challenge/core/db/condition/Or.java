package talkdesk.challenge.core.db.condition;

public class Or extends BinaryOperator {
  public Or(Condition left, Condition right) {
    super(left, right);
  }
}

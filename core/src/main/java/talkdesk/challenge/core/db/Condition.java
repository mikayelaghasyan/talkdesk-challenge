package talkdesk.challenge.core.db;

public interface Condition {
  static Condition truth() { return new Truth(); }
  static Condition falsity() { return new Falsity(); }

  default Condition and(Condition other) { return new And(this, other); }
  default Condition or(Condition other) { return new Or(this, other); }
  default Condition not() { return new Not(this); }

  static Condition eq(String field, Object value) { return new Eq(field, value); }
  static Condition gt(String field, Object value) { return new Gt(field, value); }
  static Condition lt(String field, Object value) { return new Lt(field, value); }
}

class Truth implements Condition {}
class Falsity implements Condition {}

abstract class BinaryOperator implements Condition {
  Condition left;
  Condition right;

  public BinaryOperator(Condition left, Condition right) {
    this.left = left;
    this.right = right;
  }
}

class And extends BinaryOperator {
  public And(Condition left, Condition right) {
    super(left, right);
  }
}

class Or extends BinaryOperator {
  public Or(Condition left, Condition right) {
    super(left, right);
  }
}

abstract class UnaryOperator implements Condition {
  Condition operand;

  public UnaryOperator(Condition operand) {
    this.operand = operand;
  }
}

class Not extends UnaryOperator {
  public Not(Condition op) {
    super(op);
  }
}

abstract class Comparison implements Condition {
  String field;
  Object value;

  public Comparison(String field, Object value) {
    this.field = field;
    this.value = value;
  }
}

class Eq extends Comparison {
  public Eq(String field, Object value) {
    super(field, value);
  }
}

class Gt extends Comparison {
  public Gt(String field, Object value) {
    super(field, value);
  }
}

class Lt extends Comparison {
  public Lt(String field, Object value) {
    super(field, value);
  }
}

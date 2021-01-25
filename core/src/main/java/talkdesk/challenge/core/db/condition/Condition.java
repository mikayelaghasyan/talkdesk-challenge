package talkdesk.challenge.core.db.condition;

public interface Condition {
  static Condition truth() { return new Truth(); }

  default Condition and(Condition other) { return new And(this, other); }
  default Condition or(Condition other) { return new Or(this, other); }
  default Condition not() { return new Not(this); }

  static Condition eq(String field, Object value) { return new Eq(field, value); }
  static Condition gt(String field, Object value) { return new Gt(field, value); }
  static Condition lt(String field, Object value) { return new Lt(field, value); }
}


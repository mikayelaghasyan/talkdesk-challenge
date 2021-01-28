package talkdesk.challenge.apigateway.validation;

import io.vertx.core.Future;

import java.util.List;

public abstract class ComparisonValidator<U extends Comparable<U>> extends DependencyValidator<U> {
  public ComparisonValidator(ValueProvider<U> valueProvider, List<String> properties) {
    super(valueProvider, properties);
  }

  protected String firstProperty() {
    return properties.get(0);
  }

  protected String secondProperty() {
    return properties.get(1);
  }

  @Override
  protected Future<Void> validate(List<U> values) {
    return validate(values.get(0), values.get(1));
  }

  protected abstract Future<Void> validate(U first, U second);
}

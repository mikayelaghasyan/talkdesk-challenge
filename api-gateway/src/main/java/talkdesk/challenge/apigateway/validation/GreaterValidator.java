package talkdesk.challenge.apigateway.validation;

import io.vertx.core.Future;

import java.util.List;

public class GreaterValidator<U extends Comparable<U>> extends ComparisonValidator<U> {
  public GreaterValidator(ValueProvider<U> valueProvider, List<String> properties) {
    super(valueProvider, properties);
  }

  @Override
  protected Future<Void> validate(U first, U second) {
    if (first.compareTo(second) <= 0) {
      return Future.failedFuture(new RuntimeException(String.format("%s is not greater than %s", firstProperty(), secondProperty())));
    } else {
      return Future.succeededFuture();
    }
  }
}

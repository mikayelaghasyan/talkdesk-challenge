package talkdesk.challenge.apigateway.validation;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.json.schema.common.AsyncValidator;
import io.vertx.json.schema.common.BaseAsyncValidator;
import io.vertx.json.schema.common.ValidatorContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CompositeValidator extends BaseAsyncValidator {
  private final List<AsyncValidator> validators;

  public CompositeValidator(AsyncValidator... validators) {
    this.validators = Arrays.asList(validators);
  }

  @Override
  public Future<Void> validateAsync(ValidatorContext validatorContext, Object o) {
    return CompositeFuture.all(validators.stream()
      .map(validator -> validator.validateAsync(validatorContext, o))
      .collect(Collectors.toList()))
      .map(x -> null);
  }
}

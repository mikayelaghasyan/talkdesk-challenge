package talkdesk.challenge.apigateway.validation;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.json.schema.ValidationException;
import io.vertx.json.schema.common.BaseAsyncValidator;
import io.vertx.json.schema.common.ValidatorContext;

import java.util.List;
import java.util.stream.Collectors;

public abstract class DependencyValidator<U> extends BaseAsyncValidator {
  private final ValueProvider<U> valueProvider;
  protected final List<String> properties;

  public DependencyValidator(ValueProvider<U> valueProvider, List<String> properties) {
    this.valueProvider = valueProvider;
    this.properties = properties;
  }

  @Override
  public Future<Void> validateAsync(ValidatorContext validatorContext, Object o) {
    JsonObject jsonObject = (JsonObject) o;
    List<U> values = properties.stream()
      .map(jsonObject::getString)
      .map(valueProvider::provideValue)
      .collect(Collectors.toList());

    return validate(values)
      .compose(x -> Future.succeededFuture(),
        t -> Future.failedFuture(ValidationException
          .createException(t.getMessage(), "dependency", o)));
  }

  protected abstract Future<Void> validate(List<U> values);

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static DependencyValidator create(JsonObject schema) {
    List<String> properties = schema.getJsonArray("properties").stream()
      .map(x -> (String)x).collect(Collectors.toList());

    String dataFormat = schema.getString("data-format");
    ValueProvider<?> valueProvider = ValueProvider.create(dataFormat);

    String relation = schema.getString("relation");
    switch (relation) {
      case "greater":
        return new GreaterValidator(valueProvider, properties);
      case "not-equal":
        return new NotEqualValidator(valueProvider, properties);
      default:
        throw new RuntimeException("Unknown relation: " + relation);
    }
  }
}

package talkdesk.challenge.apigateway.validation;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.pointer.JsonPointer;
import io.vertx.json.schema.SchemaException;
import io.vertx.json.schema.common.*;

public class DependencyValidatorFactory implements ValidatorFactory {
  public static final String KEY = "x-dependencies";

  @Override
  public Validator createValidator(JsonObject schema, JsonPointer scope, SchemaParserInternal parser, MutableStateValidator validator) throws SchemaException {
    return new CompositeValidator(schema.getJsonArray(KEY).stream()
      .map(dep -> DependencyValidator.create((JsonObject) dep))
      .toArray(AsyncValidator[]::new));
  }

  @Override
  public boolean canConsumeSchema(JsonObject schema) {
    return schema.containsKey(KEY);
  }
}

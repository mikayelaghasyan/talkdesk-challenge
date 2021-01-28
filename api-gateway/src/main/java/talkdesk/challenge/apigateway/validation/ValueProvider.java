package talkdesk.challenge.apigateway.validation;

import java.util.Objects;

public abstract class ValueProvider<U> {
  public static ValueProvider<?> create(String dataFormat) {
    if (Objects.isNull(dataFormat)) {
      return new IdentityValueProvider();
    }
    switch (dataFormat) {
      case "date":
      case "date-time":
        return new DateValueProvider();
      default:
        return new IdentityValueProvider();
    }
  }

  abstract public U provideValue(String rawValue);
}

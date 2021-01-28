package talkdesk.challenge.apigateway.validation;

import java.time.OffsetDateTime;

public class DateValueProvider extends ValueProvider<OffsetDateTime> {
  @Override
  public OffsetDateTime provideValue(String rawValue) {
    return OffsetDateTime.parse(rawValue);
  }
}

package talkdesk.challenge.apigateway.validation;

public class IdentityValueProvider extends ValueProvider<String> {
  @Override
  public String provideValue(String rawValue) {
    return rawValue;
  }
}

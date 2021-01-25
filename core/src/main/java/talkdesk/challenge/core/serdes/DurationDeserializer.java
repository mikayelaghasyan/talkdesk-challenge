package talkdesk.challenge.core.serdes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class DurationDeserializer extends StdDeserializer<Duration> {
  public DurationDeserializer() {
    this(null);
  }

  public DurationDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public Duration deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    return Duration.of(jsonParser.getValueAsLong(), ChronoUnit.SECONDS);
  }
}

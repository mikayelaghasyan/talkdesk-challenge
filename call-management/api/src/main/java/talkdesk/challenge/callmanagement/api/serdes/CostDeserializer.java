package talkdesk.challenge.callmanagement.api.serdes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import talkdesk.challenge.callmanagement.api.model.Cost;

import java.io.IOException;

public class CostDeserializer extends StdDeserializer<Cost> {
  public CostDeserializer() {
    this(null);
  }

  public CostDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public Cost deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    return Cost.of(jsonParser.getValueAsLong());
  }
}

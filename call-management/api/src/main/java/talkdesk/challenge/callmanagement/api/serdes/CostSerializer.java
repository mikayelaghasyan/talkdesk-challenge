package talkdesk.challenge.callmanagement.api.serdes;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import talkdesk.challenge.callmanagement.api.model.Cost;

import java.io.IOException;

public class CostSerializer extends StdSerializer<Cost> {
  public CostSerializer() {
    this(null);
  }

  public CostSerializer(Class<Cost> t) {
    super(t);
  }

  @Override
  public void serialize(Cost cost, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeNumber(cost.longValue());
  }
}

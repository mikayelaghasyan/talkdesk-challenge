package talkdesk.challenge.callmanagement.api.serdes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import talkdesk.challenge.callmanagement.api.model.Phone;

import java.io.IOException;

public class PhoneDeserializer extends StdDeserializer<Phone> {
  public PhoneDeserializer() {
    this(null);
  }

  public PhoneDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public Phone deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    return Phone.of(jsonParser.getValueAsString());
  }
}

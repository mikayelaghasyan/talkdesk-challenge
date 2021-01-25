package talkdesk.challenge.callmanagement.api.serdes;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import talkdesk.challenge.callmanagement.api.model.Phone;

import java.io.IOException;

public class PhoneSerializer extends StdSerializer<Phone> {
  public PhoneSerializer() {
    this(null);
  }

  public PhoneSerializer(Class<Phone> t) {
    super(t);
  }

  @Override
  public void serialize(Phone phone, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeString(phone.number());
  }
}

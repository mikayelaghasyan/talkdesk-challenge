package talkdesk.challenge.callmanagement.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import talkdesk.challenge.callmanagement.api.serdes.PhoneDeserializer;
import talkdesk.challenge.callmanagement.api.serdes.PhoneSerializer;

import java.util.Objects;

@JsonSerialize(using = PhoneSerializer.class)
@JsonDeserialize(using = PhoneDeserializer.class)
public class Phone {
  private String number;

  public Phone() {
  }

  public String number() {
    return number;
  }

  public void number(String number) {
    this.number = number;
  }

  private Phone(String number) {
    this.number = number;
  }

  public static Phone of(String number) {
    return new Phone(number);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Phone phone = (Phone) o;
    return number.equals(phone.number);
  }

  @Override
  public int hashCode() {
    return Objects.hash(number);
  }

  @Override
  public String toString() {
    return String.format("%s", number());
  }
}

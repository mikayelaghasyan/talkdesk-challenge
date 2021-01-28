package talkdesk.challenge.callmanagement.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import talkdesk.challenge.callmanagement.api.serdes.PhoneDeserializer;
import talkdesk.challenge.callmanagement.api.serdes.PhoneSerializer;

import java.util.Objects;
import java.util.regex.Pattern;

@JsonSerialize(using = PhoneSerializer.class)
@JsonDeserialize(using = PhoneDeserializer.class)
public class Phone {
  private static Pattern pattern = Pattern.compile("^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s./0-9]*$");

  private String number;

  public Phone() {
  }

  public String number() {
    return number;
  }

  public void number(String number) {
    this.number = validated(number);
  }

  private Phone(String number) {
    this.number = validated(number);
  }

  public static Phone of(String number) {
    return new Phone(number);
  }

  private String validated(String number) {
    if (!pattern.matcher(number).matches()) {
      throw new IllegalStateException("Invalid phone format");
    }
    return number;
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

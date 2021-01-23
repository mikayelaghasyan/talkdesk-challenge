package talkdesk.challenge.callmanagement.model;

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
}

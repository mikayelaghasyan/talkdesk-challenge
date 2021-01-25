package talkdesk.challenge.callstatistics.api.model;

import talkdesk.challenge.callmanagement.api.model.CallType;
import talkdesk.challenge.callmanagement.api.model.Cost;
import talkdesk.challenge.callmanagement.api.model.Phone;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

public class Stat {
  private UUID uuid;
  private LocalDate date;
  private final Map<CallType, DurationByCallType> totalDurationByCallType = new HashMap<>();
  private long totalNumberOfCalls = 0L;
  private final Map<Phone, NumberOfCallsByPhone> numberOfCallsByCaller = new HashMap<>();
  private final Map<Phone, NumberOfCallsByPhone> numberOfCallsByCallee = new HashMap<>();
  private Cost totalCost = Cost.ofZero();

  public Stat() {
    this(null);
  }

  public Stat(LocalDate date) {
    this.uuid = UUID.randomUUID();
    this.date = date;
    this.totalDurationByCallType.put(CallType.INBOUND, new DurationByCallType(CallType.INBOUND, Duration.ZERO));
    this.totalDurationByCallType.put(CallType.OUTBOUND, new DurationByCallType(CallType.OUTBOUND, Duration.ZERO));
  }
  public LocalDate date() {
    return date;
  }

  public void date(LocalDate date) {
    this.date = date;
  }

  public Map<CallType, DurationByCallType> totalDurationByCallType() {
    return totalDurationByCallType;
  }

  public DurationByCallType totalDurationByCallType(CallType type) {
    return totalDurationByCallType.get(type);
  }

  public long totalNumberOfCalls() {
    return totalNumberOfCalls;
  }

  public void totalNumberOfCalls(long totalNumberOfCalls) {
    this.totalNumberOfCalls = totalNumberOfCalls;
  }

  public Map<Phone, NumberOfCallsByPhone> numberOfCallsByCaller() {
    return numberOfCallsByCaller;
  }

  public NumberOfCallsByPhone numberOfCallsByCaller(Phone phone) {
    return numberOfCallsByCaller.computeIfAbsent(phone, k -> new NumberOfCallsByPhone(k));
  }

  public Map<Phone, NumberOfCallsByPhone> numberOfCallsByCallee() {
    return numberOfCallsByCallee;
  }

  public NumberOfCallsByPhone numberOfCallsByCallee(Phone phone) {
    return numberOfCallsByCallee.computeIfAbsent(phone, k -> new NumberOfCallsByPhone(k));
  }

  public Cost totalCost() {
    return totalCost;
  }

  public void totalCost(Cost totalCost) {
    this.totalCost = totalCost;
  }

  public void incrementTotalNumberOfCalls() {
    totalNumberOfCalls += 1;
  }

  public void decrementTotalNumberOfCalls() {
    if (totalNumberOfCalls == 0) {
      throw new IllegalStateException("Total number of calls can't be negative");
    }
    this.totalNumberOfCalls -= 1;
  }

  public void increaseTotalCost(Cost cost) {
    this.totalCost = this.totalCost.add(cost);
  }

  public void decreaseTotalCost(Cost cost) {
    if (this.totalCost.compareTo(cost) < 0) {
      throw new IllegalStateException("Total cost can't be negative");
    }
    this.totalCost = this.totalCost.remove(cost);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Stat that = (Stat) o;
    return totalNumberOfCalls == that.totalNumberOfCalls && date.equals(that.date) && totalDurationByCallType.equals(that.totalDurationByCallType) && numberOfCallsByCaller.equals(that.numberOfCallsByCaller) && numberOfCallsByCallee.equals(that.numberOfCallsByCallee) && totalCost.equals(that.totalCost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(date, totalDurationByCallType, totalNumberOfCalls, numberOfCallsByCaller, numberOfCallsByCallee, totalCost);
  }

  public static class DurationByCallType {
    private CallType type;
    private Duration duration;

    public DurationByCallType() {
    }

    public DurationByCallType(CallType type, Duration duration) {
      this.type = type;
      this.duration = duration;
    }

    public CallType type() {
      return type;
    }

    public void type(CallType type) {
      this.type = type;
    }

    public Duration duration() {
      return duration;
    }

    public void duration(Duration duration) {
      this.duration = duration;
    }

    public void increaseDuration(Duration duration) {
      this.duration = this.duration.plus(duration);
    }

    public void decreaseDuration(Duration duration) {
      if (this.duration.compareTo(duration) < 0) {
        throw new IllegalStateException("Duration can't be negative");
      }
      this.duration = this.duration.minus(duration);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      DurationByCallType that = (DurationByCallType) o;
      return type == that.type && duration.equals(that.duration);
    }

    @Override
    public int hashCode() {
      return Objects.hash(type, duration);
    }
  }

  public static class NumberOfCallsByPhone {
    private Phone phone;
    private long numberOfCalls;

    public NumberOfCallsByPhone() {
    }

    public NumberOfCallsByPhone(Phone phone) {
      this.phone = phone;
      this.numberOfCalls = 0L;
    }

    public Phone phone() {
      return phone;
    }

    public void phone(Phone phone) {
      this.phone = phone;
    }

    public long numberOfCalls() {
      return numberOfCalls;
    }

    public void numberOfCalls(long numberOfCalls) {
      this.numberOfCalls = numberOfCalls;
    }

    public void incrementNumberOfCalls() {
      numberOfCalls += 1;
    }

    public void decrementNumberOfCalls() {
      if (numberOfCalls == 0) {
        throw new IllegalStateException("Number of calls can't be negative");
      }
      this.numberOfCalls -= 1;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      NumberOfCallsByPhone that = (NumberOfCallsByPhone) o;
      return numberOfCalls == that.numberOfCalls && phone.equals(that.phone);
    }

    @Override
    public int hashCode() {
      return Objects.hash(phone, numberOfCalls);
    }
  }
}

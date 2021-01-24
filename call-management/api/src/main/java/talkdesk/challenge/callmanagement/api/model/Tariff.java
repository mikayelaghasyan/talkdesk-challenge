package talkdesk.challenge.callmanagement.api.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class Tariff {
  private Set<Rule> inboundRules;
  private Set<Rule> outboundRules;

  public Tariff() {
  }

  public Set<Rule> inboundRules() {
    return inboundRules;
  }

  public void inboundRules(Set<Rule> inboundRules) {
    this.inboundRules = inboundRules;
  }

  public Set<Rule> outboundRules() {
    return outboundRules;
  }

  public void outboundRules(Set<Rule> outboundRules) {
    this.outboundRules = outboundRules;
  }

  public Cost calculateCost(LocalDateTime startedAt, LocalDateTime endedAt, CallType type) {
    Set<Rule> rules = null;
    switch (type) {
      case INBOUND:
        rules = inboundRules;
        break;
      case OUTBOUND:
        rules = outboundRules;
        break;
    }

    List<Rule> sortedRules = rules.stream()
      .sorted(Comparator.comparingLong(Rule::upTo))
      .collect(Collectors.toList());

    long remainingMins = startedAt.until(endedAt, ChronoUnit.MINUTES);
    long from = 0L;
    Cost totalCost = Cost.ofZero();
    Iterator<Rule> ruleIterator = sortedRules.iterator();
    while (remainingMins > 0) {
      if (!ruleIterator.hasNext()) {
        break;
      }
      Rule rule = ruleIterator.next();
      long mins = Math.min(remainingMins, rule.upTo() - from);
      from = rule.upTo();
      totalCost = totalCost.add(rule.cost().multiply(mins));
      remainingMins -= mins;
    }
    return totalCost;
  }

  public static class Rule {
    private Long upTo;
    private Cost cost;

    public Rule() {
    }

    public Long upTo() {
      return Optional.ofNullable(upTo).orElse(Long.MAX_VALUE);
    }

    public void upTo(Long upTo) {
      this.upTo = upTo;
    }

    public Cost cost() {
      return cost;
    }

    public void cost(Cost cost) {
      this.cost = cost;
    }
  }
}

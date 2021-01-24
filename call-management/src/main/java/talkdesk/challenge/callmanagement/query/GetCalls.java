package talkdesk.challenge.callmanagement.query;

import talkdesk.challenge.callmanagement.model.CallType;
import talkdesk.challenge.core.model.Order;
import talkdesk.challenge.core.model.Page;

import java.util.Objects;

public class GetCalls {
  private CallType type;
  private Page page;
  private Order order;

  public GetCalls() {
  }

  public CallType type() {
    return type;
  }

  public void type(CallType type) {
    this.type = type;
  }

  public Page page() {
    return page;
  }

  public void page(Page page) {
    this.page = page;
  }

  public Order order() {
    return order;
  }

  public void order(Order order) {
    this.order = order;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GetCalls getCalls = (GetCalls) o;
    return page.equals(getCalls.page) && order.equals(getCalls.order);
  }

  @Override
  public int hashCode() {
    return Objects.hash(page, order);
  }
}

package talkdesk.challenge.core.model;

import java.util.List;
import java.util.Objects;

public class Paginated<U> {
  private U[] items;
  private Page page;
  private Long totalCount;

  public Paginated() {
  }

  public Paginated(U[] items, Page page, Long totalCount) {
    this.items = items;
    this.page = page;
    this.totalCount = totalCount;
  }

  public U[] items() {
    return items;
  }

  public void items(U[] items) {
    this.items = items;
  }

  public Page page() {
    return page;
  }

  public void page(Page page) {
    this.page = page;
  }

  public Long totalCount() {
    return totalCount;
  }

  public void totalCount(Long totalCount) {
    this.totalCount = totalCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Paginated<?> paginated = (Paginated<?>) o;
    return items.equals(paginated.items) && page.equals(paginated.page) && totalCount.equals(paginated.totalCount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(items, page, totalCount);
  }
}

package talkdesk.challenge.core.model;

import java.util.Objects;

public class Page {
  private long number;
  private long size;

  public Page() {
  }

  public Page(long number, long size) {
    this.number = number;
    this.size = size;
  }

  public long number() {
    return number;
  }

  public void number(long number) {
    this.number = number;
  }

  public long size() {
    return size;
  }

  public void size(long size) {
    this.size = size;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Page page = (Page) o;
    return number == page.number && size == page.size;
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, size);
  }
}

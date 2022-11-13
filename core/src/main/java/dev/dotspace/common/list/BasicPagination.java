package dev.dotspace.common.list;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
@Accessors(fluent = true)
public class BasicPagination<T> implements Pagination<T> {
  private final int elementsPerPage;
  private @NotNull List<T> list = new ArrayList<>();

  public BasicPagination(final int elementsPerPage) {
    if (elementsPerPage <= 0) {
      throw new IllegalArgumentException("Can't build a BasicPagination with this elementsPerPage int{" + elementsPerPage + "}.");
    }
    this.elementsPerPage = elementsPerPage;
  }

  @Override
  public int pagesQuantity() {
    return BasicPagination.pageSize(this.list(), this.elementsPerPage());
  }

  @Override
  public void content(@NotNull Collection<T> collection) {
    this.list = new ArrayList<>(collection);
  }

  public void clear() {
    this.list.clear();
  }

  @Override
  public @NotNull List<T> elements(int index) throws IndexOutOfBoundsException {
    return this.subList(index, false);
  }

  @Override
  public @NotNull List<T> elementsChecked(int index) {
    return this.subList(index, true);
  }

  private @NotNull List<T> subList(int index,
                                   final boolean checkIndex) {
    final List<T> clonedList = new ArrayList<>(this.list());

    if (clonedList.isEmpty()) {
      return Collections.emptyList();
    }

    index = Math.max(0, index);
    final int maxIndex = pageSize(clonedList, this.elementsPerPage()) - 1;

    if (!checkIndex && index > maxIndex) {
      throw new IndexOutOfBoundsException("Index " + index + " is out of the list range {0," + (maxIndex) + "}");
    } else {
      index = Math.min(index, maxIndex);
    }

    final int begin = index * this.elementsPerPage();
    return new ArrayList<>(this.list().subList(begin, Math.min(begin + this.elementsPerPage(), clonedList.size())));
  }

  private static int pageSize(@NotNull final List<?> list, int elementsPerPage) {
    return (int) Math.ceil((double) list.size() / (double) elementsPerPage);
  }
}

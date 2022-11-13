package dev.dotspace.common.list;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public interface Pagination<T> {
  /**
   * @return
   */
  int elementsPerPage();

  /**
   * @return
   */
  int pagesQuantity();

  /**
   * @param collection
   */
  void content(@NotNull final Collection<T> collection);

  /**
   * @param index starts at 0 [index: 0 -> means page 1]
   * @return
   * @throws IndexOutOfBoundsException if index is not present in current list
   */
  @NotNull List<T> elements(final int index) throws IndexOutOfBoundsException;

  /**
   * @param index starts at 0 [index: 0 -> means page 1]
   * @return
   */
  @NotNull List<T> elementsChecked(final int index);
}

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
   * Update content of this object -> set elements to be used for the pages
   *
   * @param collection to update elements with
   */
  void content(@NotNull final Collection<T> collection);

  /**
   * @param index starts at 0 [index: 0 -> means page 1]
   * @return
   * @throws IndexOutOfBoundsException if index is not present in current list
   */
  @NotNull List<T> elements(final int index) throws IndexOutOfBoundsException;
}

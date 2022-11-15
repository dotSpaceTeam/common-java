package dev.dotspace.common.list;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public interface Pagination<T> {
  /**
   * Sets amount of elements to display on one page.
   *
   * @return amount of elements.
   */
  int elementsPerPage();

  /**
   * Get the maximum amount of pages
   *
   * @return amount of pages can be generated from content.
   */
  int pagesQuantity();

  /**
   * Update content of this object -> set elements to be used for the pages.
   *
   * @param collection to update elements with.
   */
  void content(@NotNull final Collection<T> collection);

  /**
   * Get content of index (page).
   *
   * @param index starts at 0 [index: 0 -> means page 1].
   * @return list with elements of index
   * @throws IndexOutOfBoundsException if index is not present in current list.
   */
  @NotNull List<T> elements(final int index) throws IndexOutOfBoundsException;
}

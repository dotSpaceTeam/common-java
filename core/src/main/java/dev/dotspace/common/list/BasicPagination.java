package dev.dotspace.common.list;

import dev.dotspace.common.SpaceObjects;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Create a operation {@link Pagination} with minimalistic methods.
 *
 * @param <T> generic type of elements for the local list
 */
@Getter
@Accessors(fluent = true)
public class BasicPagination<T> implements Pagination<T> {
  /**
   * see {@link Pagination#elementsPerPage()}
   */
  private final int elementsPerPage;
  private @NotNull List<T> list = new ArrayList<>();

  /**
   * Create a new {@link BasicPagination} with a set amount of objects per page.
   *
   * @param elementsPerPage defines how many objects should be shown on one page.
   */
  public BasicPagination(final int elementsPerPage) {
    if (elementsPerPage <= 0) {
      throw new IllegalArgumentException(
        String.format("Can't build a BasicPagination with this elementsPerPage int{%d}.", elementsPerPage));
    }
    this.elementsPerPage = elementsPerPage;
  }

  /**
   * Create a new {@link BasicPagination} with a set amount of object per page and content.
   * <p>
   * Uses {@link BasicPagination#BasicPagination(int)} and {@link BasicPagination#content(Collection)}
   *
   * @param elementsPerPage defines how many objects should be shown on one page.
   * @param collection      to update elements with
   */
  public BasicPagination(final int elementsPerPage,
                         @NotNull final Collection<T> collection) {
    this(elementsPerPage);
    this.content(collection);
  }

  /**
   * see {@link Pagination#pagesQuantity()}.
   */
  @Override
  public int pagesQuantity() {
    return BasicPagination.pageSize(this.list(), this.elementsPerPage());
  }

  /**
   * see {@link Pagination#content(Collection)}.
   */
  @Override
  public void content(@NotNull Collection<T> collection) {
    this.list = new ArrayList<>(SpaceObjects.throwIfNull(collection));
  }

  /**
   * Clear the content of this object -> empty pages.
   */
  public void clear() {
    this.list.clear();
  }

  /**
   * see {@link Pagination#elements(int)}.
   */
  @Override
  public @NotNull List<T> elements(int index) throws IndexOutOfBoundsException {
    return this.subList(index, false);
  }

  /**
   * Same as {@link Pagination#elements(int)} with a check if given index is out of bounds.
   * Example:
   * A list with 10 elements and 3 elements per page can be spilt into 4 pages (index 0-3).
   */
  public @NotNull List<T> elementsChecked(int index) {
    return this.subList(index, true);
  }

  /**
   * Get the elements from a specific page
   *
   * @param index      to get elements from
   * @param checkIndex if true, system will check if index is out of bounds and will use the highest index with elements
   * @return the list with elements of this index
   */
  private @NotNull List<T> subList(int index,
                                   final boolean checkIndex) {
    final List<T> clonedList = new ArrayList<>(this.list());

    if (clonedList.isEmpty()) { //Return an empty list if clonedList is empty -> safe performance.
      return Collections.emptyList();
    }

    index = Math.max(0, index);
    final int maxIndex = pageSize(clonedList, this.elementsPerPage()) - 1;

    if (!checkIndex && index > maxIndex) {
      throw new IndexOutOfBoundsException("Index " + index + " is out of the list range {0," + (maxIndex) + "}"); //If check is disabled and index is out of rang throw error.
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

package dev.dotspace.common;

import dev.dotspace.common.concurrent.FutureResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class with {@link Collection} operations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE) //Block class construction.
public final class SpaceCollections {
  /**
   * Get a random object of collection.
   *
   * @param collection to get random object from.
   * @param <ELEMENT>  generic type of {@link Collection}.
   * @return random object of collection wrapped in {@link Optional}.
   * -> Optional is empty if {@link Collection} is null or empty.
   */
  @SuppressWarnings("unchecked")
  public static <ELEMENT> @Nullable ELEMENT random(@Nullable final Collection<ELEMENT> collection) {
    if (collection == null || collection.isEmpty()) {
      return null; //Return null to safe performance.
    }
    return (ELEMENT) SpaceCollections.randomImplementation(collection.toArray());
  }

  /**
   * Get a random element of array of any type.
   *
   * @param array     to get random element of.
   * @param <ELEMENT> generic type of element to get random.
   * @return random drawn element or null if array is null or empty.
   */
  public static <ELEMENT> @Nullable ELEMENT random(@Nullable final ELEMENT[] array) {
    if (array == null || array.length == 0) {
      return null; //Return null to safe performance.
    }
    return SpaceCollections.randomImplementation(array);
  }

  /**
   * Get a random object of collection async. This operation could be used for big collections with many values.
   * The completion of the {@link CompletableFuture} holds the random number.
   *
   * @param collection to get random object from.
   * @param <ELEMENT>  generic type of {@link Collection}.
   * @return completableFuture with will be filled with the random object. Object could be null if collection is null.
   * or empty or if the given object is null in list.
   */
  public static <ELEMENT> @NotNull FutureResponse<ELEMENT> randomAsync(@Nullable final Collection<ELEMENT> collection) {
    return new FutureResponse<ELEMENT>().completeAsync(() -> SpaceCollections.random(collection)); //Complete the future in a separate thread
  }

  public static <ELEMENT> @NotNull FutureResponse<ELEMENT> randomAsync(@Nullable final ELEMENT[] array) {
    return new FutureResponse<ELEMENT>().completeAsync(() -> SpaceCollections.random(array)); //Complete the future in a separate thread
  }

  /**
   * Implementation to get random object of array. Position is calculated by using a {@link java.util.Random}.
   *
   * @param array     to get random element from.
   * @param <ELEMENT> generic type of elements of array.
   * @return random element, null if given array has less than 1 member.
   */
  private static <ELEMENT> @Nullable ELEMENT randomImplementation(@Nullable final ELEMENT[] array) {
    final int index = array.length > 1 ? (int) (ThreadLocalRandom.current().nextDouble() * array.length) : 0; //Calculate random index to get from collection.
    return array[index]; //Return random object of list.
  }
}

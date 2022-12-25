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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpaceCollections {
  /**
   * Get a random object of collection. Object wrapped in {@link Optional}.
   *
   * @param collection to get random object from.
   * @param <T>        generic type of {@link Collection}.
   * @return random object of collection wrapped in {@link Optional}.
   * -> Optional is empty if {@link Collection} is null or empty.
   */
  @SuppressWarnings("unchecked")
  public static <T> @NotNull Optional<@NotNull T> random(@Nullable final Collection<T> collection) {
    if (collection == null || collection.isEmpty()) {
      return Optional.empty(); //Return empty Optional to safe performance
    }
    final int index = collection.size() > 1 ? (int) (ThreadLocalRandom.current().nextDouble() * collection.size()) : 0; //Calculate random index to get from collection
    return (Optional<T>) Optional.ofNullable(collection.toArray()[index]); //Return random object of list
  }

  /**
   * Get a random object of collection async. This operation could be used for big collections with many values.
   * The completion of the {@link CompletableFuture} holds the random number.
   *
   * @param collection to get random object from.
   * @param <T>        generic type of {@link Collection}.
   * @return completableFuture with will be filled with the random object. Object could be null if collection is null.
   * or empty or if the given object is null in list.
   */
  public static <T> @NotNull FutureResponse<T> asyncRandom(@Nullable final Collection<T> collection) {
    return new FutureResponse<T>().completeAsync(() -> SpaceCollections.random(collection).orElse(null)); //Complete the future in a separate thread
  }
}

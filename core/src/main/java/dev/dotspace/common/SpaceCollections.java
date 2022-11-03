package dev.dotspace.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class with {@link Collection} operations
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpaceCollections {
  private final static ExecutorService SERVICE = Executors.newCachedThreadPool();

  /**
   * Get a random object of collection. Object wrapped in {@link Optional}
   *
   * @param collection to get random object from
   * @param <T>        generic type of {@link Collection}
   * @return random object of collection wrapped in {@link Optional}
   * -> Optional is empty if {@link Collection} is null or empty
   */
  @SuppressWarnings("unchecked")
  public static <T> @NotNull Optional<T> random(final Collection<T> collection) {
    if (collection == null || collection.isEmpty()) {
      return Optional.empty(); //Return empty Optional to safe performance
    }
    final int index = collection.size() > 1 ? (int) (ThreadLocalRandom.current().nextDouble() * collection.size()) : 0; //Calculate random index to get from collection
    return (Optional<T>) Optional.of(collection.toArray()[index]); //Return random object of list
  }

  /**
   * Get a random object of collection async. This operation could be used for big collections with many values.
   * The completion of the {@link CompletableFuture} holds the random number
   *
   * @param collection to get random object from
   * @param <T>        generic type of {@link Collection}
   * @return completableFuture with will be filled with the random object
   */
  public static <T> @NotNull CompletableFuture<@NotNull T> asyncRandom(final Collection<T> collection) {
    final CompletableFuture<@NotNull T> completableFuture = new CompletableFuture<>(); //Create new completableFuture
    SERVICE.execute(() -> completableFuture.complete(SpaceCollections.random(collection).orElseThrow())); //Complete the future in a separate thread
    return completableFuture;
  }
}

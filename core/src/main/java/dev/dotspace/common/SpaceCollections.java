package dev.dotspace.common;

import dev.dotspace.common.annotation.LibraryInformation;
import dev.dotspace.common.response.CompletableResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Methods for simplifying collections and arrays.
 * <p>
 * Class with {@link Collection} operations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE) //Block class construction.
@LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
public final class SpaceCollections {
  /**
   * Get a random object of collection.
   *
   * @param collection to get random object from.
   * @param <TYPE>     generic type of {@link Collection}.
   * @return random object of collection wrapped in {@link Optional}.
   * -> Optional is empty if {@link Collection} is null or empty.
   */
  @SuppressWarnings("unchecked")
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static <TYPE> @Nullable TYPE random(@Nullable final Collection<TYPE> collection) {
    if (collection == null || collection.isEmpty()) {
      return null; //Return null to safe performance.
    }
    return (TYPE) collection.toArray()[LibraryCommonUtils.calculateRandomIndex(collection.size())];
  }

  /**
   * Get a random object of collection async. This operation could be used for big collections with many values.
   * The completion of the {@link CompletableFuture} holds the random number.
   *
   * @param collection to get random object from.
   * @param <TYPE>     generic type of {@link Collection}.
   * @return completableFuture with will be filled with the random object. Object could be null if collection is null.
   * or empty or if the given object is null in list.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static <TYPE> @NotNull CompletableResponse<TYPE> randomAsync(@Nullable final Collection<TYPE> collection) {
    return new CompletableResponse<TYPE>().completeAsync(() -> SpaceCollections.random(collection)); //Complete the future in a separate thread
  }
}

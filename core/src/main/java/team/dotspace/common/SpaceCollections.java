package team.dotspace.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class with {@link Collection} operations
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpaceCollections {
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
}

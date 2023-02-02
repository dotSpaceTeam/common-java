package dev.dotspace.examples.common;

import dev.dotspace.common.SpaceCollections;

public class CollectionsExamples {

  public void example() {
    final String[] array = {"This", "is", "a", "test!"};

    String value = SpaceCollections.random(array); //Get random directly.

    SpaceCollections.randomAsync(array).ifPresent(s -> {
      //Get random using FutureResponse implementation.
    });

  }

}

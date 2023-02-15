package dev.dotspace.examples.common;

import dev.dotspace.common.SpaceCollections;

import java.util.ArrayList;
import java.util.List;

public class CollectionsExamples {

  public void example() {
    final List<String> names = new ArrayList<>();

    names.add("Joe");
    names.add("Jeff");

    String value = SpaceCollections.random(names); //Get random directly.

    SpaceCollections.randomAsync(names).ifPresent(s -> {
      //Get random using FutureResponse implementation.
    });
  }
}

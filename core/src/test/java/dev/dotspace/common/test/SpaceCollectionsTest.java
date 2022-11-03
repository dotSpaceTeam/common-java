package dev.dotspace.common.test;

import dev.dotspace.common.SpaceCollections;

import java.util.HashSet;
import java.util.Set;

public class SpaceCollectionsTest {


  public static void main(String[] args) {
    final Set<Integer> integers = new HashSet<>() {
      {
        for (int i = 0; i < 10; i++) {
          this.add(i);
        }
      }
    };

    SpaceCollections.random(integers).ifPresent(SpaceCollectionsTest::a);
    SpaceCollections.asyncRandom(integers).thenAccept(SpaceCollectionsTest::a);
  }

  public static void a(Integer integer) {
    System.out.println("Thread: " + Thread.currentThread().getName() + " Value:" + integer);
  }

}

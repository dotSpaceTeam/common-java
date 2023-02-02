package dev.dotspace.examples.data.wrapper;

import dev.dotspace.data.wrapper.instance.WrapperType;
import dev.dotspace.data.wrapper.manager.ManagerOptions;
import dev.dotspace.data.wrapper.manager.WrapperManager;

public final class WrapperTestDriver {

  public static void main(String[] args) throws InterruptedException {
    final WrapperManager<ExampleWrapper> wrapperHolder = new WrapperManager<>("Names", ExampleWrapper.class);

    wrapperHolder.implementWrapper(new StorageWrapper());
    wrapperHolder.implementWrapper(new CacheWrapper());

    wrapperHolder.query(WrapperType.STORAGE, exampleWrapper -> {
      return exampleWrapper.setName("TestName", "TestValue");
    });
    wrapperHolder.query(WrapperType.STORAGE, exampleWrapper -> {
      return exampleWrapper.setName("TestName2", "TestValue");
    });

    wrapperHolder.query(WrapperType.STORAGE).orElseThrow(() -> new NullPointerException()).setName("TestName3", "TestValue");

    Thread.sleep(2000L);

    wrapperHolder
      .query(WrapperType.CACHE, exampleWrapper -> exampleWrapper.getName("TestName"), ManagerOptions.QUERY_ASYNCHRONOUS)
      .ifPresent(s -> {
        System.out.println("[Cache] present: " + s);
      });

    wrapperHolder
      .useCacheIfPresent(exampleWrapper -> exampleWrapper.getName("TestName"), ManagerOptions.QUERY_ASYNCHRONOUS)
      .ifPresentAsync(nameValue -> {
        System.out.println("[Cache_if_present] present: " + nameValue);
      });

    wrapperHolder
      .useCacheIfPresent(exampleWrapper -> exampleWrapper.getName("TestName3"), ManagerOptions.QUERY_ASYNCHRONOUS)
      .ifPresentAsync(nameValue -> {
        System.out.println("[Cache_if_present] present: " + nameValue);
      });

    wrapperHolder
      .useFirstResponse(exampleWrapper -> exampleWrapper.getName("TestName"))
      .ifPresentAsync(s -> {
        System.out.println("[Fastest] present: " + s);
      });

    Thread.sleep(20000L);
  }

}

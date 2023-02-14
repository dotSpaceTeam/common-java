package dev.dotspace.examples.data.wrapper;

import dev.dotspace.common.wrapper.instance.WrapperType;
import dev.dotspace.common.wrapper.manager.QueryOptions;
import dev.dotspace.common.wrapper.manager.WrapperManager;

import java.util.stream.Collectors;

public final class WrapperTestDriver {

  public static void main(String[] args) throws InterruptedException {
    final WrapperManager<ExampleWrapper> wrapperHolder = new WrapperManager<>("Names", ExampleWrapper.class);

    wrapperHolder.implementWrapper(new StorageWrapper());
    wrapperHolder.implementWrapper(new CacheWrapper());

    for (int i = 0; i < 100; i++) {
      int finalI = i;
      wrapperHolder.storeToAll(exampleWrapper -> {
        return exampleWrapper.setName("TestName" + finalI, "TestValue" + finalI);
      }, QueryOptions.async().disableAutoCache(false));
    }


    wrapperHolder
      .cacheIfPresent(exampleWrapper -> exampleWrapper.getName("TestName3"), QueryOptions.async())
      .ifPresentAsync(nameValue -> {
        System.out.println("[Cache_if_present] present: " + nameValue);
      });

    wrapperHolder.cacheIfPresent(ExampleWrapper::getNames, QueryOptions.async())
      .ifPresent(nameValues -> {
        System.out.println("[Cache_if_present] List: " + nameValues.stream().map(NameValue::key).collect(Collectors.joining(",")));
      });

    Thread.sleep(5000L);

    wrapperHolder
      .cacheIfPresent(exampleWrapper -> exampleWrapper.getName("TestName5"))
      .ifPresent(nameValue -> {
        System.out.println("[Cache_if_present] delay present: " + nameValue);
      })
      .ifExceptionally(throwable -> {
        System.out.println("Error");
      });


   /* wrapperHolder
      .query(WrapperType.CACHE, exampleWrapper -> exampleWrapper.getName("TestName"), QueryOptions.async())
      .ifPresent(s -> {
        System.out.println("[Cache] present: " + s);
      });

    */

   /* wrapperHolder
      .cacheIfPresent(exampleWrapper -> exampleWrapper.getName("TestName"), QueryOptions.async())
      .ifPresentAsync(nameValue -> {
        System.out.println("[Cache_if_present] present: " + nameValue);
      });

    */

    wrapperHolder.query(WrapperType.CACHE, exampleWrapper -> exampleWrapper.setName("", "")).ifPresent(nameValue -> {

    });

    wrapperHolder
      .firstResponse(exampleWrapper -> exampleWrapper.getName("TestName1"), QueryOptions.async())
      .ifPresentAsync(s -> {
        System.out.println("[Fastest] present: " + s);
      });

    wrapperHolder
      .firstResponse(WrapperType.STORAGE, exampleWrapper -> exampleWrapper.getName("TestName1"), QueryOptions.async())
      .ifPresentAsync(s -> {
        System.out.println("[Fastest] present storage: " + s);
      });

    Thread.sleep(20000L);
  }

}

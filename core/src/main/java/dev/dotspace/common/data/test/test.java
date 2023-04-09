package dev.dotspace.common.data.test;

import dev.dotspace.common.data.DataManager;
import dev.dotspace.common.data.Manager;
import dev.dotspace.common.data.WrapperType;

public class test {

  public static void main(String[] args) {
    final Manager<ProfileWrapper> wrapper = DataManager.create(ProfileWrapper.class)
      .register(new CacheWrapper(), WrapperType.VOLATILE)
      .register(new StorageWrapper(), WrapperType.PERSISTENT);

    System.out.println("run");

    wrapper.query(WrapperType.PERSISTENT)
      .first(profileWrapper -> profileWrapper.post(new Profile("Test", 5)))
      .ifPresent(profile -> {
        System.out.println("Saved: " + profile);
      });

    wrapper.query()
      .collect(profileWrapper -> profileWrapper.post(new Profile("test", 24)))
      .ifPresent(results -> {
        System.out.println(results);
      });

    wrapper.query()
      .first(profileWrapper -> profileWrapper.get("Test"))
      .ifPresent(profile -> {
        System.out.println(profile);
      });


  }

}

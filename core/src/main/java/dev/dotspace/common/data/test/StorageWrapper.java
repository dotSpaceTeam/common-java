package dev.dotspace.common.data.test;


import dev.dotspace.common.response.CompletableResponse;

import java.util.HashMap;
import java.util.Map;

public class StorageWrapper implements ProfileWrapper {
  private final Map<String, Profile> profileMap = new HashMap<>();

  @Override
  public CompletableResponse<Profile> get(String name) {
    return new CompletableResponse<Profile>().completeAsync(() -> {
      try {
        Thread.sleep(2000L);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      return this.profileMap.get(name);
    });
  }

  @Override
  public CompletableResponse<Profile> post(Profile profile) {
    return new CompletableResponse<Profile>().completeAsync(() -> {
      try {
        Thread.sleep(5000L);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      this.profileMap.put(profile.name(), profile);
      return profile;
    });
  }
}

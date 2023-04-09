package dev.dotspace.common.data.test;

import dev.dotspace.common.data.Wrapper;
import dev.dotspace.common.response.CompletableResponse;

public interface ProfileWrapper extends Wrapper {

  CompletableResponse<Profile> get(String name);

  CompletableResponse<Profile> post(Profile profile);

}

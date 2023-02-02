package dev.dotspace.examples.data.wrapper;

import dev.dotspace.common.concurrent.FutureResponse;
import dev.dotspace.data.wrapper.instance.Wrapper;

public interface ExampleWrapper extends Wrapper {

    FutureResponse<NameValue> getName(String key);

    FutureResponse<NameValue> setName(String key, String value);

}

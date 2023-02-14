package dev.dotspace.examples.data.wrapper;

import dev.dotspace.common.concurrent.FutureResponse;
import dev.dotspace.common.wrapper.instance.Wrapper;

import java.util.List;

public interface ExampleWrapper extends Wrapper {

    FutureResponse<NameValue> getName(String key);

    FutureResponse<List<NameValue>> getNames();

    FutureResponse<NameValue> setName(String key, String value);

}

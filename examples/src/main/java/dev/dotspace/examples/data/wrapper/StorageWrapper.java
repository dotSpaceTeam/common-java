package dev.dotspace.examples.data.wrapper;

import dev.dotspace.common.concurrent.FutureResponse;

import dev.dotspace.common.wrapper.instance.WrapperData;
import dev.dotspace.common.wrapper.instance.WrapperType;
import dev.dotspace.common.wrapper.method.WrapperMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@WrapperData(name = "Storage", wrapperType = WrapperType.STORAGE)
public class StorageWrapper implements ExampleWrapper {
  private final ConcurrentMap<String, NameValue> simulatedDatabase = new ConcurrentHashMap<>();

  @WrapperMethod
  @Override
  public FutureResponse<NameValue> getName(String key) {
    return new FutureResponse<NameValue>().composeContentAsync(objectResponseContent -> {
      Optional.ofNullable(simulatedDatabase.get(key))
        .ifPresentOrElse(objectResponseContent::content, () -> {
          objectResponseContent.throwable(new NullPointerException("Not present storage!"));
        });
    });
  }

  @WrapperMethod
  @Override
  public FutureResponse<List<NameValue>> getNames() {
    return new FutureResponse<List<NameValue>>().complete(new ArrayList<>(this.simulatedDatabase.values()));
  }

  @WrapperMethod
  @Override
  public FutureResponse<NameValue> setName(String key, String value) {
    return new FutureResponse<NameValue>().composeContentAsync(objectResponseContent -> {
      final NameValue nameValue = new NameValue(key, value);
      this.simulatedDatabase.put(key, nameValue);
      objectResponseContent.content(nameValue);
    });
  }
}

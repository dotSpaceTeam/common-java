package dev.dotspace.examples.data.wrapper;

import dev.dotspace.common.concurrent.FutureResponse;

import dev.dotspace.data.wrapper.instance.WrapperData;
import dev.dotspace.data.wrapper.instance.WrapperType;
import dev.dotspace.data.wrapper.method.MethodType;
import dev.dotspace.data.wrapper.method.WrapperMethod;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@WrapperData(name = "Storage", wrapperType = WrapperType.STORAGE)
public class StorageWrapper implements ExampleWrapper {
  private final ConcurrentMap<String, NameValue> simulatedDatabase = new ConcurrentHashMap<>();

  @WrapperMethod(methodType = MethodType.READ)
  @Override
  public FutureResponse<NameValue> getName(String key) {
    return new FutureResponse<NameValue>().composeContentAsync(objectResponseContent -> {
      Optional.ofNullable(simulatedDatabase.get(key))
        .ifPresentOrElse(objectResponseContent::content, () -> {
          objectResponseContent.throwable(new NullPointerException("Not present storage!"));
        });
    });
  }

  @WrapperMethod(methodType = MethodType.MODIFY)
  @Override
  public FutureResponse<NameValue> setName(String key, String value) {
    return new FutureResponse<NameValue>().composeContentAsync(objectResponseContent -> {
      final NameValue nameValue = new NameValue(key, value);
      this.simulatedDatabase.put(key, nameValue);
      objectResponseContent.content(nameValue);
    });
  }
}

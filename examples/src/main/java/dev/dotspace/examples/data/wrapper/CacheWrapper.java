package dev.dotspace.examples.data.wrapper;

import dev.dotspace.common.concurrent.FutureResponse;
import dev.dotspace.common.wrapper.instance.WrapperData;
import dev.dotspace.common.wrapper.instance.WrapperType;
import dev.dotspace.common.wrapper.method.WrapperMethod;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@WrapperData(name = "Cache", wrapperType = WrapperType.CACHE)
public class CacheWrapper implements ExampleWrapper {
  private final ConcurrentMap<String, NameValue> simulatedRedisCache = new ConcurrentHashMap<>();

  @WrapperMethod
  @Override
  public FutureResponse<NameValue> getName(String key) {
    return new FutureResponse<NameValue>().composeContentAsync(objectResponseContent -> {

      Optional.ofNullable(simulatedRedisCache.get(key))
        .ifPresentOrElse(objectResponseContent::content, () -> {
          objectResponseContent.throwable(new NullPointerException("Not present cache!"));
        });

    });
  }

  @WrapperMethod
  @Override
  public FutureResponse<List<NameValue>> getNames() {
    return new FutureResponse<List<NameValue>>().complete(new ArrayList<>(this.simulatedRedisCache.values()));
  }

  @WrapperMethod
  @Override
  public FutureResponse<NameValue> setName(String key, String value) {
    return FutureResponse.exception(new NullPointerException("Cache can't change values."));
  }

  @Override
  public boolean latestProcessedObject(@Nullable Object object) {
    if (!(object instanceof NameValue nameValue)) {
      return false;
    }
    this.simulatedRedisCache.put(nameValue.key(), nameValue);
    return true;
  }
}

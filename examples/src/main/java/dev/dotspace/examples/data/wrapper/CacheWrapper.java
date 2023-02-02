package dev.dotspace.examples.data.wrapper;

import dev.dotspace.common.concurrent.FutureResponse;
import dev.dotspace.data.wrapper.instance.WrapperData;
import dev.dotspace.data.wrapper.instance.WrapperType;
import dev.dotspace.data.wrapper.method.MethodType;
import dev.dotspace.data.wrapper.method.WrapperMethod;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@WrapperData(name = "Cache", wrapperType = WrapperType.CACHE)
public class CacheWrapper implements ExampleWrapper {
  private final ConcurrentMap<String, NameValue> simulatedRedisCache = new ConcurrentHashMap<>();

  @WrapperMethod(methodType = MethodType.READ)
  @Override
  public FutureResponse<NameValue> getName(String key) {
    return new FutureResponse<NameValue>().composeContentAsync(objectResponseContent -> {

      Optional.ofNullable(simulatedRedisCache.get(key))
        .ifPresentOrElse(objectResponseContent::content, () -> {
          objectResponseContent.throwable(new NullPointerException("Not present cache!"));
        });

    });
  }

  @WrapperMethod(methodType = MethodType.MODIFY)
  @Override
  public FutureResponse<NameValue> setName(String key, String value) {
    return new FutureResponse<NameValue>().composeContentAsync(objectResponseContent -> {
      final NameValue nameValue = new NameValue(key, value);
      this.simulatedRedisCache.put(key, nameValue);
      objectResponseContent.content(nameValue);
    });
  }

  @Override
  public void latestProcessedObject(@NotNull Object object) {
    if (!(object instanceof NameValue nameValue)) {
      return;
    }
    this.simulatedRedisCache.put(nameValue.key(), nameValue);
  }
}

package dev.dotspace.data.wrapper.manager;

import dev.dotspace.common.concurrent.FutureResponse;
import dev.dotspace.data.wrapper.instance.Wrapper;
import dev.dotspace.data.wrapper.instance.WrapperData;
import dev.dotspace.data.wrapper.instance.WrapperInstance;
import dev.dotspace.data.wrapper.instance.WrapperType;
import dev.dotspace.data.wrapper.method.WrapperInstanceMethod;
import dev.dotspace.data.wrapper.method.WrapperMethod;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public final class WrapperManager<WRAPPER extends Wrapper> {
  private final Class<WRAPPER> wrapperClass;
  private final List<WrapperInstance<WRAPPER>> wrappers = new ArrayList<>();
  private boolean active = true;

  public WrapperManager(Class<WRAPPER> wrapperClass) {
    this.wrapperClass = wrapperClass;
  }

  public boolean implementWrapper(final WRAPPER wrapper) {
    this.checkIfDeactivated();
    final WrapperData wrapperInfo = wrapper.getClass().getAnnotation(WrapperData.class);

    if (!this.wrapperClass.isInstance(wrapper)) {
      // LOGGER.error("Given wrapper is not instance of {}.", this.wrapperClass);
      return false;
    }

    if (wrapperInfo == null) {
      // LOGGER.error("Given wrapper has no @DataWrapperInfo annotation is null.");
      return false;
    }

    final List<WrapperInstanceMethod> methods = new ArrayList<>();
    for (final Method method : wrapper.getClass().getMethods()) {
      final WrapperMethod wrapperMethod = method.getAnnotation(WrapperMethod.class);

      if (wrapperMethod == null) {
        continue;
      }

      if (!method.getGenericReturnType().getTypeName().startsWith("dev.dotspace.common.concurrent.FutureResponse")) {
        // LOGGER.error("Method is not FutureResponse<?>");
        continue;
      }

      final WrapperInstanceMethod wrapperInstanceMethod = new WrapperInstanceMethod(method.getGenericReturnType(),
        method.getGenericParameterTypes(),
        method.getName(),
        wrapperMethod.methodType(),
        method);

      methods.add(wrapperInstanceMethod);

      //   LOGGER.info("{}", wrapperInstanceMethod);
    }

    if (!this.wrappers.isEmpty()) {
      for (final WrapperInstance<WRAPPER> wrapperInstance : this.wrappers) {
        if (new HashSet<>(wrapperInstance.methods()).containsAll(methods) &&
          new HashSet<>(methods).containsAll(wrapperInstance.methods())) {
          continue;
        }
        this.deactivate();
        return false;
      }
    }

    this.wrappers.add(new WrapperInstance<>(wrapper, wrapperInfo.name(), wrapperInfo.wrapperType(), wrapperInfo.priority(), methods));
    // LOGGER.info("Successfully added wrapper[{}] to manager methods=[{}].",
    //  wrapperInfo.name(),
    //  methods.stream().map(WrapperInstanceMethod::name).collect(Collectors.joining(", ")));
    return true;
  }

  public <TYPE> FutureResponse<TYPE> useFirstResponse(@NotNull final Function<WRAPPER, FutureResponse<TYPE>> wrapperFutureResponseFunction,
                                                      @NotNull final ManagerOptions... managerOptions) {
    this.checkIfDeactivated();
    final FutureResponse<TYPE> typeFutureResponse = new FutureResponse<>();

    for (WrapperInstance<WRAPPER> wrapper : wrappers) {
      wrapperFutureResponseFunction
        .apply(wrapper.wrapper())
        .ifPresent(type -> {
          if (typeFutureResponse.future().isDone()) {
            return;
          }
          typeFutureResponse.complete(type);
        })
        .ifExceptionally(throwable -> {
          //   LOGGER.warn("Wrapper answered with error: {}", throwable.getMessage());
        });
    }

    return typeFutureResponse;
  }

  public <TYPE> FutureResponse<TYPE> useCacheIfPresent(final Function<WRAPPER, FutureResponse<TYPE>> function,
                                                       @NotNull final ManagerOptions... managerOptions) {
    this.checkIfDeactivated();
    return new FutureResponse<TYPE>().composeContentAsync(typeResponseContent -> {
      final List<WrapperInstance<WRAPPER>> cacheInstances = this.filteredAndOrdered(WrapperType.CACHE).toList();
      final Set<WrapperInstance<WRAPPER>> responseCount = new HashSet<>();

      final Runnable runnable = () -> {
        if (responseCount.size() != cacheInstances.size()) {
          return;
        }

        this.query(WrapperType.STORAGE, function).ifPresent(typeResponseContent::content);
      };

      for (final WrapperInstance<WRAPPER> wrapper : cacheInstances) {
        function
          .apply(wrapper.wrapper())
          .ifPresent(typeResponseContent::content)
          .ifAbsentOrExceptionally(() -> {
            responseCount.add(wrapper);
            runnable.run();
          }, throwable -> {
            responseCount.add(wrapper);
            runnable.run();
          });
      }
    });
  }

  public @NotNull Optional<WRAPPER> query(final WrapperType wrapperType) {
    this.checkIfDeactivated();
    return this.filteredAndOrdered(wrapperType)
      .findFirst()
      .map(WrapperInstance::wrapper);
  }

  public @NotNull WRAPPER queryElseTrow(final WrapperType wrapperType) {
    return this.query(wrapperType).orElseThrow();
  }

  public <TYPE> FutureResponse<TYPE> query(final WrapperType wrapperType,
                                           final Function<WRAPPER, FutureResponse<TYPE>> function,
                                           @NotNull final ManagerOptions... managerOptions) {
    final WRAPPER wrapper = this.queryElseTrow(wrapperType);
    final FutureResponse<TYPE> type = function.apply(wrapper);
    type
      .ifExceptionallyAsync(throwable -> {
        //   LOGGER.error("Completed with error. " + throwable.getMessage());
      })
      .ifPresentAsync(presentType -> {
        if (wrapperType == WrapperType.CACHE) {
          return; //Return cache can't override cache values.
        }
        for (final WrapperInstance<WRAPPER> wrapperWrapperInstance : this.wrappers) {
          if (wrapperWrapperInstance.wrapperType() == WrapperType.STORAGE) {
            continue;
          }
          try {
            wrapperWrapperInstance.wrapper().latestProcessedObject(presentType);
            //  LOGGER.info("Cached value: {} in wrapper {}.", presentType, wrapperWrapperInstance.name());
          } catch (final Throwable throwable) {
            throwable.printStackTrace();
          }
        }
      });
    return type;
  }

  private @NotNull Stream<WrapperInstance<WRAPPER>> filteredAndOrdered(final WrapperType wrapperType) {
    return this.wrappers
      .stream()
      .filter(wrapperWrapperR -> wrapperWrapperR.wrapperType() == wrapperType)
      .sorted((o1, o2) -> Byte.compare(o2.priority(), o1.priority()));
  }

  private void deactivate() {
    this.active = false;
    this.wrappers.clear();

    System.out.println("Deactivated class of an error.");
  }

  private void checkIfDeactivated() {
    if (!active) {
      throw new RuntimeException("Manager already deactivated.");
    }
  }

  public List<WrapperInstance<WRAPPER>> getWrappers() {
    return wrappers;
  }
}

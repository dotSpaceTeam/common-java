package dev.dotspace.common.wrapper.manager;

import dev.dotspace.common.concurrent.FutureResponse;
import dev.dotspace.common.concurrent.ResponseContent;
import dev.dotspace.common.wrapper.instance.WrapperInstance;
import dev.dotspace.common.wrapper.method.WrapperMethod;
import dev.dotspace.common.wrapper.instance.Wrapper;
import dev.dotspace.common.wrapper.instance.WrapperData;
import dev.dotspace.common.wrapper.instance.WrapperType;
import dev.dotspace.common.wrapper.method.WrapperInstanceMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class WrapperManager<WRAPPER extends Wrapper> {
  private final Logger logger;
  private final Class<WRAPPER> wrapperClass;
  private final List<WrapperInstance<WRAPPER>> wrappers = new ArrayList<>();
  private boolean active = true;

  public WrapperManager(@NotNull final String name,
                        @NotNull final Class<WRAPPER> wrapperClass) {
    this.logger = LoggerFactory.getLogger("WrapperManager(%s)".formatted(name));
    this.wrapperClass = wrapperClass;
  }

  /**
   * @param wrapper
   * @return
   */
  public boolean implementWrapper(@NotNull final WRAPPER wrapper) {
    this.checkIfDeactivated();

    final WrapperData wrapperInfo = wrapper.getClass().getAnnotation(WrapperData.class);

    if (!this.wrapperClass.isInstance(wrapper)) {
      this.logger.error("Given wrapper is not instance of {}.", this.wrapperClass);
      return false;
    }

    if (wrapperInfo == null) {
      this.logger.error("Given wrapper has no @DataWrapperInfo annotation is null.");
      return false;
    }

    final List<WrapperInstanceMethod> methods = new ArrayList<>();
    for (final Method method : wrapper.getClass().getMethods()) {
      final WrapperMethod wrapperMethod = method.getAnnotation(WrapperMethod.class);

      if (wrapperMethod == null) {
        continue;
      }

      if (!method.getGenericReturnType().getTypeName().startsWith("dev.dotspace.common.concurrent.FutureResponse")) {
        this.logger.error("Method is not FutureResponse<?>");
        continue;
      }

      final WrapperInstanceMethod wrapperInstanceMethod = new WrapperInstanceMethod(method.getGenericReturnType(),
        method.getGenericParameterTypes(),
        method.getName(),
        method);

      methods.add(wrapperInstanceMethod);
      this.logger.info("{}", wrapperInstanceMethod);
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
    this.logger.info("Successfully added wrapper[{}] to manager methods=[{}].",
      wrapperInfo.name(),
      methods.stream().map(WrapperInstanceMethod::name).collect(Collectors.joining(", ")));
    return true;
  }

  public <TYPE> void storeToAll(@NotNull final Function<WRAPPER, FutureResponse<TYPE>> wrapperFutureResponseFunction,
                                @Nullable final QueryOptions queryOptions) {
    this.filteredAndOrdered(WrapperType.STORAGE).forEach(wrapperWrapperInstance -> {
      this.queryImplementation(wrapperWrapperInstance, wrapperFutureResponseFunction, queryOptions);
    });
  }

  public <TYPE> void storeToAll(@NotNull final Function<WRAPPER, FutureResponse<TYPE>> wrapperFutureResponseFunction) {
    this.storeToAll(wrapperFutureResponseFunction, null);
  }

  /**
   * Ask every present wrapper for data. The fastest data will be used for the response {@link FutureResponse}.
   *
   * @param wrapperFutureResponseFunction function to get the methods from the wrappers and their answers.
   * @param <TYPE>                        generic type of response.
   * @return
   */
  public <TYPE> FutureResponse<TYPE> firstResponse(@NotNull final Function<WRAPPER, FutureResponse<TYPE>> wrapperFutureResponseFunction,
                                                   @Nullable final QueryOptions queryOptions) {
    return this.firstResponseImplementation(this.wrappers, wrapperFutureResponseFunction, queryOptions);
  }

  public <TYPE> FutureResponse<TYPE> firstResponse(@NotNull final Function<WRAPPER, FutureResponse<TYPE>> wrapperFutureResponseFunction) {
    return this.firstResponse(wrapperFutureResponseFunction, null);
  }

  public <TYPE> FutureResponse<TYPE> firstResponse(@NotNull final WrapperType wrapperType,
                                                   @NotNull final Function<WRAPPER, FutureResponse<TYPE>> wrapperFutureResponseFunction,
                                                   @Nullable final QueryOptions queryOptions) {
    return this.firstResponseImplementation(this.filteredAndOrdered(wrapperType).toList(), wrapperFutureResponseFunction, queryOptions);
  }


  public <TYPE> FutureResponse<TYPE> firstResponse(@NotNull final WrapperType wrapperType,
                                                   @NotNull final Function<WRAPPER, FutureResponse<TYPE>> wrapperFutureResponseFunction) {
    return this.firstResponse(wrapperType, wrapperFutureResponseFunction, null);
  }

  /**
   * Implementation for firstResponse.
   *
   * @param wrapperFutureResponseFunction
   * @param queryOptions
   * @param <TYPE>
   * @return
   */
  private <TYPE> FutureResponse<TYPE> firstResponseImplementation(@NotNull final List<WrapperInstance<WRAPPER>> wrappers,
                                                                  @NotNull final Function<WRAPPER, FutureResponse<TYPE>> wrapperFutureResponseFunction,
                                                                  @Nullable QueryOptions queryOptions) {
    this.checkIfDeactivated(); //Check if this manager is still active.
    final QueryOptions finalQueryOptions = queryOptions == null ? QueryOptions.DEFAULT : queryOptions;

    final FutureResponse<TYPE> typeFutureResponse = new FutureResponse<>(); //Response of this function.

    for (WrapperInstance<WRAPPER> wrapper : wrappers) {
      final FutureResponse<TYPE> functionResponse = wrapperFutureResponseFunction.apply(wrapper.wrapper()); //Get response of wrapper.

      final Consumer<TYPE> presentConsumer = type -> { // Consumer to fill if value is present from function.
        if (typeFutureResponse.done()) { //Return if response of this function is already done.
          return;
        }
        typeFutureResponse.complete(type); //Complete function of this method.

        if (!finalQueryOptions.disableLogger()) {
          this.logger.info("{} answered as fastest wrapper.", wrapper.name());
        }
      };

      final Consumer<Throwable> throwableConsumer = throwable -> { //Handle exception consumer.
        this.logger.warn("Wrapper answered with error: {}", throwable.getMessage());
      };

      if (finalQueryOptions.asynchronous()) {
        functionResponse
          .ifPresentAsync(presentConsumer)
          .ifExceptionallyAsync(throwableConsumer);
      } else {
        functionResponse
          .ifPresent(presentConsumer)
          .ifExceptionally(throwableConsumer);
      }
    }

    return typeFutureResponse;
  }


  public <TYPE> FutureResponse<TYPE> cacheIfPresent(@NotNull final Function<WRAPPER, FutureResponse<TYPE>> function,
                                                    @Nullable final QueryOptions queryOptions) {
    return this.cacheIfPresentImplementation(function, queryOptions);
  }

  public <TYPE> FutureResponse<TYPE> cacheIfPresent(@NotNull final Function<WRAPPER, FutureResponse<TYPE>> function) {
    return this.cacheIfPresent(function, null);
  }

  private <TYPE> FutureResponse<TYPE> cacheIfPresentImplementation(@NotNull final Function<WRAPPER, FutureResponse<TYPE>> function,
                                                                   @Nullable final QueryOptions queryOptions) {
    this.checkIfDeactivated(); //Check if this manager is still active.
    final QueryOptions finalQueryOptions = queryOptions == null ? QueryOptions.DEFAULT : queryOptions;

    final FutureResponse<TYPE> typeFutureResponse = new FutureResponse<>(); //Response of this function.

    final Consumer<ResponseContent<TYPE>> consumer = response -> {
      final List<WrapperInstance<WRAPPER>> cacheInstances = this.filteredAndOrdered(WrapperType.CACHE).toList();
      final AtomicInteger parsedCacheWrappers = new AtomicInteger();

      final Runnable informationFromStorage = () -> {
        if (parsedCacheWrappers.incrementAndGet() < cacheInstances.size()) {
          return;
        }

        this.logger.info("Value not present in cache wrappers, pull from storage.");
        this.query(WrapperType.STORAGE, function, finalQueryOptions)
          .ifPresent(response::content);
      };

      if (cacheInstances.isEmpty()) {
        informationFromStorage.run();
        this.logger.error("No cache present. Don't use this method.");
      } else {
        for (final WrapperInstance<WRAPPER> wrapper : cacheInstances) {
          if (response.content() != null) {
            break;
          }

          try {
            function
              .apply(wrapper.wrapper())
              .get()
              .ifPresentOrElse(response::content, informationFromStorage);
          } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
          }
        }
      }
    };

    if (finalQueryOptions.asynchronous()) {
      typeFutureResponse.composeContentAsync(consumer);
    } else {
      typeFutureResponse.composeContent(consumer);
    }

    return typeFutureResponse;
  }

  /**
   * Search directly for a wrapper for the appropriate type. if none exists, {@link Optional#empty()}.
   *
   * @param wrapperType to search for.
   * @return
   */
  public @NotNull Optional<WrapperInstance<WRAPPER>> query(@NotNull final WrapperType wrapperType) {
    this.checkIfDeactivated(); //Check if this manager is still active.

    return this.filteredAndOrdered(wrapperType).findFirst();
  }

  public <TYPE> FutureResponse<TYPE> query(@NotNull final WrapperType wrapperType,
                                           @NotNull final Function<WRAPPER, FutureResponse<TYPE>> function,
                                           @Nullable QueryOptions queryOptions) {
    return this.queryImplementation(this.query(wrapperType).orElseThrow(), function, queryOptions);
  }

  public <TYPE> FutureResponse<TYPE> query(@NotNull final WrapperType wrapperType,
                                           @NotNull final Function<WRAPPER, FutureResponse<TYPE>> function) {
    return this.query(wrapperType, function, null);
  }

  /**
   * Implementation for query implementation.
   *
   * @param wrapperInstance
   * @param function
   * @param queryOptions
   * @param <TYPE>
   * @return
   */
  private <TYPE> FutureResponse<TYPE> queryImplementation(@NotNull final WrapperInstance<WRAPPER> wrapperInstance,
                                                          @NotNull final Function<WRAPPER, FutureResponse<TYPE>> function,
                                                          @Nullable QueryOptions queryOptions) {
    final FutureResponse<TYPE> type = function.apply(wrapperInstance.wrapper());
    final QueryOptions finalQueryOptions = queryOptions == null ? QueryOptions.DEFAULT : queryOptions;

    final Consumer<Throwable> throwableConsumer = throwable -> {
      this.logger.error("Completed with error. " + throwable.getMessage());
    };

    final Consumer<TYPE> presentConsumer = presentType -> {
      if (wrapperInstance.wrapperType() == WrapperType.CACHE || finalQueryOptions.disableAutoCache() /*No direct cache.*/) {
        return; //Return cache can't override cache values.
      }
      for (final WrapperInstance<WRAPPER> wrapperWrapperInstance : this.wrappers) {
        if (wrapperWrapperInstance.wrapperType() == WrapperType.STORAGE) {
          continue;
        }
        try {
          final boolean storedCacheValue = wrapperWrapperInstance.wrapper().latestProcessedObject(presentType);

          if (storedCacheValue && !finalQueryOptions.disableLogger()) {
            this.logger.info("Cached value: {} in wrapper {}.", presentType, wrapperWrapperInstance.name());
          }
        } catch (final Throwable throwable) {
          throwable.printStackTrace();
        }
      }
    };

    if (finalQueryOptions.asynchronous()) {
      type
        .ifExceptionallyAsync(throwableConsumer)
        .ifPresentAsync(presentConsumer);
    } else {
      type
        .ifExceptionally(throwableConsumer)
        .ifPresent(presentConsumer);
    }
    return type;
  }

  private @NotNull Stream<WrapperInstance<WRAPPER>> filtered(@NotNull final WrapperType wrapperType) {
    return this.wrappers
      .stream()
      .filter(wrapperWrapperR -> wrapperWrapperR.wrapperType() == wrapperType);
  }

  private @NotNull Stream<WrapperInstance<WRAPPER>> filteredAndOrdered(@NotNull final WrapperType wrapperType) {
    return this.filtered(wrapperType)
      .sorted((o1, o2) -> Byte.compare(o2.priority(), o1.priority()));
  }

  private void deactivate() {
    this.active = false;
    this.wrappers.clear();

    System.out.println("Deactivated class of an error.");
  }

  private void checkIfDeactivated() {
    if (!this.active) {
      throw new RuntimeException("Manager already deactivated.");
    }
  }
}

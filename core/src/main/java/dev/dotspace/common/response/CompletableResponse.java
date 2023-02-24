package dev.dotspace.common.response;

import dev.dotspace.common.SpaceObjects;
import dev.dotspace.common.SpaceThrowable;
import dev.dotspace.common.exception.MismatchException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("unused") //Some methods are meant to be for the library -> Suppress idea warnings.
public final class CompletableResponse<TYPE> implements Response<TYPE> {
  private final @NotNull ExecutorService executorService;
  private volatile @NotNull State state;
  private volatile @Nullable TYPE response;
  private volatile @Nullable Throwable throwable;
  private volatile @NotNull ResponseFunction<?>[] responseFunctions;

  /**
   * Public constructor to create instance.
   */
  public CompletableResponse() {
    this(State.UNCOMPLETED);
  }

  /**
   * This constructor can be used to create an instance without explicitly defining a generic type.
   * This method is a gimmick, the class is not saved, it is only used to set the type.
   *
   * @param typeClass defines the TYPE of the instance.
   */
  public CompletableResponse(@Nullable final Class<TYPE> typeClass) {
    this();
  }

  /**
   * Create instance with defined state.
   *
   * @param state to set as start {@link State}.
   */
  private CompletableResponse(@NotNull final State state) {
    this.executorService = Executors.newCachedThreadPool();
    this.state = state;
    this.responseFunctions = new ResponseFunction[0];
  }

  /**
   * @see Response#newUncompleted()
   */
  @Override
  public @NotNull CompletableResponse<TYPE> newUncompleted() {
    return new CompletableResponse<>();
  }

  /**
   * @see Response#get()
   */
  @Override
  public @Nullable TYPE get() throws InterruptedException {
    return this.getImplementation(-1);
  }

  /**
   * @see Response#get(long)
   */
  @Override
  public @Nullable TYPE get(long nanos) throws InterruptedException {
    return this.getImplementation(nanos);
  }

  /**
   * @see Response#get(long, TimeUnit)
   */
  @Override
  public @Nullable TYPE get(long duration, @Nullable TimeUnit timeUnit) throws InterruptedException {
    return this.getImplementation(SpaceObjects.throwIfNull(timeUnit).toNanos(duration));
  }

  /**
   * Implementation for the get methods. The given time is in nanoseconds.
   * Negative numbers mean there is no interrupt time.
   *
   * @param nanoTimeout the time until the process is to be interrupted.
   * @return returns the completed value of this answer.
   * @throws InterruptedException if the process was interrupted in time. (timestamp of nanoTimeout reached).
   */
  private @Nullable TYPE getImplementation(final long nanoTimeout) throws InterruptedException {
    final boolean unlimited = nanoTimeout < 0; //True if there is no limit.
    final long interruptTime = unlimited ? Long.MAX_VALUE : System.nanoTime() + (nanoTimeout == 0 ? 1 /*Smallest value.*/ : nanoTimeout);

    while (!this.done()) {
      if (this.canceled()) { //Cancels when the response is canceled.
        break;
      }
      if (System.nanoTime() >= interruptTime) { //Throws an error when the time point is reached.
        throw this.completeExceptionallyImplementation(new InterruptedException("No value present!"));
      }
    }
    return this.response; //Returns the value of the class.
  }

  /**
   * @see Response#getNow(Supplier)
   */
  @Override
  public synchronized @Nullable TYPE getNow(@Nullable Supplier<TYPE> alternativeValue) {
    if (this.response != null) { //Response is completed.
      return this.response; //Response.
    }

    TYPE alternative = null; //Create variable.

    if (alternativeValue != null) { //If supplier is present get value.
      alternative = alternativeValue.get();
    }

    if (alternative != null) { //If supplier was not null and value is also present.
      this.completeImplementation(alternative); //Complete this response with supplied value.
    }

    return alternative;
  }

  /**
   * @see Response#cancel()
   */
  @Override
  public synchronized boolean cancel() {
    if (this.state.done()) {
      return false;
    }
    //this.throwable = new InterruptedException("Response canceled.");
    this.markAsCompleted(State.CANCELLED);
    return true;
  }

  /**
   * @see Response#complete(Object)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> complete(@Nullable TYPE type) {
    this.completeImplementation(type);
    return this;
  }

  /**
   * @see Response#completeAsync(Supplier)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> completeAsync(@Nullable Supplier<TYPE> typeSupplier) {
    this.executorService.execute(() -> {
      try {
        this.completeImplementation(SpaceObjects.throwIfNull(typeSupplier).get());
      } catch (final Throwable throwable) {
        this.completeExceptionallyImplementation(throwable);
      }
    });
    return this;
  }

  /**
   * @see Response#completeExceptionally(Throwable)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> completeExceptionally(@Nullable Throwable throwable) {
    this.completeExceptionallyImplementation(throwable);
    return this;
  }

  /**
   * @see Response#completeExceptionallyAsync(Supplier)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> completeExceptionallyAsync(@Nullable Supplier<Throwable> throwableSupplier) {
    this.executorService.execute(() -> {
      try {
        this.completeExceptionallyImplementation(SpaceObjects.throwIfNull(throwableSupplier).get());
      } catch (final NullPointerException nullPointerException) {
        this.completeExceptionally(nullPointerException);
      }
    });
    return this;
  }

  private synchronized void completeImplementation(@Nullable final TYPE completeResponse) {
    if (this.state.done()) {
      return; //Return if done.
    }
    this.response = completeResponse;
    this.markAsCompleted(completeResponse != null ? State.COMPLETED_DEFAULT : State.COMPLETED_NULL);
  }

  private synchronized <ERROR extends Throwable> @Nullable ERROR completeExceptionallyImplementation(@Nullable final ERROR throwable) {
    if (!this.state.done()) {
      this.throwable = throwable;
      this.markAsCompleted(State.COMPLETED_EXCEPTIONALLY);
    }
    return throwable;
  }

  private void markAsCompleted(@NotNull final State state) {
    this.state = state;

    for (final ResponseFunction<?> executor : this.responseFunctions) {
      executor.run(this.executorService);
    }
  }

  /**
   * @see Response#sniff(ResponseConsumer)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> sniff(@Nullable ResponseConsumer<TYPE> responseConsumer) {
    this.sniffImplementation(responseConsumer, false);
    return this;
  }

  /**
   * @see Response#sniffAsync(ResponseConsumer)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> sniffAsync(@Nullable ResponseConsumer<TYPE> responseConsumer) {
    this.sniffImplementation(responseConsumer, true);
    return this;
  }

  /**
   * Implementation for sniffing methods.
   *
   * <dl>
   *   <dt>Execute in main thread:</dt>
   *   <dd>{@link CompletableResponse#sniff(ResponseConsumer)}</dd>
   *   <dt>Execute in a new thread:</dt>
   *   <dd>{@link CompletableResponse#sniffAsync(ResponseConsumer)}</dd>
   * </dl>
   *
   * @param responseConsumer to inform if response is completed.
   * @param async            true, if the runnable is to be executed asynchronously.
   * @see ResponseConsumer
   */
  private void sniffImplementation(@Nullable final ResponseConsumer<TYPE> responseConsumer,
                                   final boolean async) {
    if (responseConsumer == null) {
      return; //Return if null. Ignore request
    }

    this.implementExecutor(new ResponseFunctionExecutor<>(
      () -> {
        try { //Catch possible errors from runnable.
          responseConsumer.accept(this.state, this.response, this.throwable);
        } catch (final Throwable throwable) {
          throwable.printStackTrace(); //Print errors.
        }
      }, async));
  }

  /**
   * @see Response#run(Runnable)
   */
  @Override
  public @NotNull Response<TYPE> run(@Nullable Runnable runnable) {
    this.runImplementation(runnable, false); //Run implementation.
    return this;
  }

  /**
   * @see Response#runAsync(Runnable)
   */
  @Override
  public @NotNull Response<TYPE> runAsync(@Nullable Runnable runnable) {
    this.runImplementation(runnable, true); //Run implementation.
    return this;
  }

  /**
   * Implementation to execute {@link Runnable} if completed.
   * <br>
   * Implementation used by:
   * <dl>
   *   <dt>Execute in main thread:</dt>
   *   <dd>{@link CompletableResponse#run(Runnable)}</dd>
   *   <dt>Execute in a new thread:</dt>
   *   <dd>{@link CompletableResponse#runAsync(Runnable)}</dd>
   * </dl>
   *
   * @param runnable to be executed if {@link Response} is completed.
   * @param async    true, if the runnable is to be executed asynchronously.
   */
  private void runImplementation(@Nullable final Runnable runnable,
                                 final boolean async) {
    if (runnable == null) {
      return; //Return, runnable is null means there is no function to run.
    }

    this.implementExecutor(new ResponseFunctionExecutor<>( //Create new executor instance.
      () -> {
        try { //Catch possible errors from runnable.
          runnable.run(); //Execute runnable.
        } catch (final Throwable throwable) {
          throwable.printStackTrace(); //Print errors.
        }
      }, async));
  }

  /**
   * @see Response#ifPresent(Consumer)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> ifPresent(@Nullable Consumer<@NotNull TYPE> consumer) {
    this.ifPresentImplementation(consumer, false); //Run implementation.
    return this;
  }

  /**
   * @see Response#ifPresentAsync(Consumer)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> ifPresentAsync(@Nullable Consumer<@NotNull TYPE> consumer) {
    this.ifPresentImplementation(consumer, true); //Run implementation.
    return this;
  }

  /**
   * Present consumer implementation.
   *
   * <dl>
   *   <dt>Execute in main thread:</dt>
   *   <dd>{@link CompletableResponse#ifPresent(Consumer)}</dd>
   *   <dt>Execute in a new thread:</dt>
   *   <dd>{@link CompletableResponse#ifPresentAsync(Consumer)}</dd>
   * </dl>
   *
   * @param consumer to inform if response is completed {@link State#COMPLETED_DEFAULT} with present object.
   * @param async    true, if the runnable is to be executed asynchronously.
   */
  private void ifPresentImplementation(@Nullable final Consumer<@NotNull TYPE> consumer,
                                       final boolean async) {
    if (consumer == null) { //If no consumer given, nothing left to do here.
      return;
    }

    this.implementExecutor(new ResponseFunctionExecutor<>(
      () -> this.response != null && this.state == State.COMPLETED_DEFAULT, //Only run if response is not null and state is COMPLETED_DEFAULT.
      () -> {
        try {
          final TYPE threadResponse = this.response; //Create local variable for thread.
          if (threadResponse == null) { //Check for thread safety.
            return;
          }
          consumer.accept(threadResponse); //Fill in consumer.
        } catch (final Throwable throwable) { //Catch errors in consumer and print them into console.
          throwable.printStackTrace();
        }
      }, async));
  }

  /**
   * @see Response#map(Function)
   */
  @Override
  public @NotNull <MAP> CompletableResponse<MAP> map(@Nullable Function<TYPE, MAP> function) {
    return this.mapImplementation(function, false);
  }

  /**
   * @see Response#mapAsync(Function)
   */
  @Override
  public @NotNull <MAP> CompletableResponse<MAP> mapAsync(@Nullable Function<TYPE, MAP> function) {
    return this.mapImplementation(function, true);
  }

  /**
   * Implementation to map response to another. {@link Function} will be executed if response was completed with value.
   * Otherwise, given function will be ignored.
   *
   * <br>
   * Condition: {@link CompletableResponse#state}: {@link State#COMPLETED_NULL}
   * <br>
   * <dl>
   *   <dt>Execute in main thread:</dt>
   *   <dd>{@link CompletableResponse#map(Function)}</dd>
   *   <dt>Execute in a new thread:</dt>
   *   <dd>{@link CompletableResponse#mapAsync(Function)}</dd>
   * </dl>
   *
   * @param function to use to map response with.
   * @param async    true, if the runnable is to be executed asynchronously.
   * @param <MAP>    type of object to map response to.
   * @return new instance of {@link CompletableResponse} with mapped response of current instance.
   */
  private <MAP> CompletableResponse<MAP> mapImplementation(@Nullable final Function<TYPE, MAP> function,
                                                           final boolean async) {
    final CompletableResponse<MAP> completableResponse = new CompletableResponse<>();
    this.implementExecutor(new ResponseFunctionExecutor<>(
      () -> this.response != null && this.state == State.COMPLETED_DEFAULT,
      () -> {
        try {
          completableResponse.complete(SpaceObjects.throwIfNull(function).apply(this.response));
        } catch (final Throwable throwable) {
          completableResponse.completeExceptionally(throwable);
        }
      }, async));
    return completableResponse;
  }

  /**
   * @see Response#filter(Predicate)
   */
  @Override
  public @NotNull Response<TYPE> filter(@Nullable Predicate<TYPE> typePredicate) {
    return this.filterImplementation(typePredicate, false);
  }

  /**
   * @see Response#filterAsync(Predicate)
   */
  @Override
  public @NotNull Response<TYPE> filterAsync(@Nullable Predicate<TYPE> typePredicate) {
    return this.filterImplementation(typePredicate, true);
  }

  /**
   * Filter response and create new {@link CompletableResponse} with filtered response.
   *
   * <br>
   * Condition: {@link CompletableResponse#state}: {@link State#COMPLETED_DEFAULT} and {@link CompletableResponse#response} is present.
   * <br>
   * <dl>
   *   <dt>Execute in main thread:</dt>
   *   <dd>{@link CompletableResponse#filter(Predicate)}</dd>
   *   <dt>Execute in a new thread:</dt>
   *   <dd>{@link CompletableResponse#filterAsync(Predicate)}</dd>
   * </dl>
   *
   * @param typePredicate to filter response if present.
   * @param async         true, if the runnable is to be executed asynchronously.
   * @return new instance of {@link CompletableResponse} with filtered response of current instance.
   */
  private @NotNull CompletableResponse<TYPE> filterImplementation(@Nullable final Predicate<TYPE> typePredicate,
                                                                  final boolean async) {
    final CompletableResponse<TYPE> completableResponse = new CompletableResponse<>();
    this.implementExecutor(new ResponseFunctionExecutor<>(
      () -> this.response != null && this.state == State.COMPLETED_DEFAULT,
      () -> {
        try {
          final TYPE currentValue = this.response;
          completableResponse.complete(currentValue != null && SpaceObjects.throwIfNull(typePredicate).test(this.response) ? currentValue : null);
        } catch (final Throwable throwable) {
          completableResponse.completeExceptionally(throwable);
        }
      }, async));
    return completableResponse;
  }

  /**
   * @see Response#ifAbsent(Runnable)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> ifAbsent(@Nullable Runnable runnable) {
    this.ifAbsentImplementation(runnable, false);
    return this;
  }

  /**
   * @see Response#ifAbsentAsync(Runnable)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> ifAbsentAsync(@Nullable Runnable runnable) {
    this.ifAbsentImplementation(runnable, true);
    return this;
  }

  /**
   * Implementation to execute {@link Runnable} if response completed with null.
   *
   * <br>
   * Condition: {@link CompletableResponse#state}: {@link State#COMPLETED_NULL}
   * <br>
   * <dl>
   *   <dt>Execute in main thread:</dt>
   *   <dd>{@link CompletableResponse#ifAbsent(Runnable)}</dd>
   *   <dt>Execute in a new thread:</dt>
   *   <dd>{@link CompletableResponse#ifAbsentAsync(Runnable)}</dd>
   * </dl>
   *
   * @param runnable to run if response completed without an error and {@link CompletableResponse#response} null.
   * @param async    true, if the runnable is to be executed asynchronously.
   */
  private void ifAbsentImplementation(@Nullable final Runnable runnable,
                                      final boolean async) {
    if (runnable == null) {//If no runnable given, nothing left to do here.
      return;
    }

    this.implementExecutor(new ResponseFunctionExecutor<>(
      () -> this.state == State.COMPLETED_NULL,
      () -> {
        try {
          runnable.run();
        } catch (final Throwable throwable) {
          throwable.printStackTrace();
        }
      }, async));
  }

  /**
   * <br>
   * Condition for supplier: {@link CompletableResponse#state}: {@link State#COMPLETED_NULL}
   * <br>
   *
   * @see Response#useIfAbsent(Supplier)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> useIfAbsent(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(typeSupplier, () -> this.state == State.COMPLETED_NULL, false);
  }

  /**
   * <br>
   * Condition for supplier: {@link CompletableResponse#state}: {@link State#COMPLETED_NULL}
   * <br>
   *
   * @see Response#useIfAbsentAsync(Supplier)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> useIfAbsentAsync(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(typeSupplier, () -> this.state == State.COMPLETED_NULL, true);
  }

  /**
   * <br>
   * Condition for supplier: {@link CompletableResponse#state}: {@link State#COMPLETED_EXCEPTIONALLY}
   * <br>
   *
   * @see Response#useIfExceptionally(Supplier)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> useIfExceptionally(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(typeSupplier, () -> this.state == State.COMPLETED_EXCEPTIONALLY, false);
  }

  /**
   * <br>
   * Condition for supplier: {@link CompletableResponse#state}: {@link State#COMPLETED_EXCEPTIONALLY}
   * <br>
   *
   * @see Response#useIfExceptionallyAsync(Supplier)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> useIfExceptionallyAsync(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(typeSupplier, () -> this.state == State.COMPLETED_EXCEPTIONALLY, true);
  }

  /**
   * <br>
   * Condition for supplier: {@link CompletableResponse#state}: {@link State#COMPLETED_NULL} or {@link State#COMPLETED_EXCEPTIONALLY}
   * <br>
   *
   * @see Response#elseUse(Supplier)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> elseUse(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(
      typeSupplier,
      () -> this.state == State.COMPLETED_NULL || this.state == State.COMPLETED_EXCEPTIONALLY,
      false);
  }

  /**
   * <br>
   * Condition for supplier: {@link CompletableResponse#state}: {@link State#COMPLETED_NULL} or {@link State#COMPLETED_EXCEPTIONALLY}
   * <br>
   *
   * @see Response#elseUseAsync(Supplier)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> elseUseAsync(@Nullable Supplier<TYPE> typeSupplier) {
    return this.useImplementation(
      typeSupplier,
      () -> this.state == State.COMPLETED_NULL || this.state == State.COMPLETED_EXCEPTIONALLY,
      true);
  }

  /**
   * <br>
   * Condition: Different conditions -> more information from caller.
   * <br>
   * <dl>
   *   <dt>Execute in main thread:</dt>
   *   <dd>{@link CompletableResponse#useIfAbsent(Supplier)}</dd>
   *   <dd>{@link CompletableResponse#useIfExceptionally(Supplier)}</dd>
   *   <dd>{@link CompletableResponse#elseUse(Supplier)}</dd>
   *   <dt>Execute in a new thread:</dt>
   *   <dd>{@link CompletableResponse#useIfAbsentAsync(Supplier)}</dd>
   *   <dd>{@link CompletableResponse#useIfExceptionallyAsync(Supplier)}</dd>
   *   <dd>{@link CompletableResponse#elseUseAsync(Supplier)}</dd>
   * </dl>
   *
   * @param typeSupplier      to get alternative response from.
   * @param checkIfExecutable condition to run alternative get.
   * @param async             true, if the runnable is to be executed asynchronously.
   * @return new instance of {@link CompletableResponse} with alternative response of current instance.
   */
  private @NotNull CompletableResponse<TYPE> useImplementation(@Nullable final Supplier<TYPE> typeSupplier,
                                                               @NotNull final Supplier<Boolean> checkIfExecutable,
                                                               final boolean async) {
    final CompletableResponse<TYPE> completableResponse = new CompletableResponse<>();
    this.implementExecutor(new ResponseFunctionExecutor<>(
      checkIfExecutable,
      () -> {
        if (this.response != null) {
          return;
        }
        try {
          completableResponse.complete(SpaceObjects.throwIfNull(typeSupplier).get());
        } catch (final Throwable throwable) {
          completableResponse.completeExceptionally(throwable);
        }
      }, async));

    return completableResponse;
  }

  /**
   * @see Response#ifExceptionally(Consumer)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> ifExceptionally(@Nullable Consumer<@Nullable Throwable> consumer) {
    this.ifExceptionallyImplementation(consumer, false);
    return this;
  }

  /**
   * @see Response#ifExceptionallyAsync(Consumer)
   */
  @Override
  public @NotNull CompletableResponse<TYPE> ifExceptionallyAsync(@Nullable Consumer<@Nullable Throwable> consumer) {
    this.ifExceptionallyImplementation(consumer, true);
    return this;
  }

  /**
   * Implementation for exceptionally consumers. Given {@link Consumer} accept the given throwable of this response.
   * Value of {@link CompletableResponse#throwable} could also be null.
   * <br>
   * Condition: {@link CompletableResponse#state}: {@link State#COMPLETED_EXCEPTIONALLY}
   * <br>
   * <dl>
   *   <dt>Execute in main thread:</dt>
   *   <dd>{@link CompletableResponse#ifExceptionally(Consumer)}</dd>
   *   <dt>Execute in a new thread:</dt>
   *   <dd>{@link CompletableResponse#ifExceptionallyAsync(Consumer)}</dd>
   * </dl>
   *
   * @param consumer to accept the throwable of the response.
   * @param async    true, if the runnable is to be executed asynchronously.
   */
  private void ifExceptionallyImplementation(@Nullable final Consumer<@Nullable Throwable> consumer,
                                             final boolean async) {
    if (consumer == null) { //Return and ignore consumer if null.
      return;
    }

    this.implementExecutor(new ResponseFunctionExecutor<>(
      () -> this.state == State.COMPLETED_EXCEPTIONALLY, //Only run if response was completed with error.
      () -> {
        try {
          consumer.accept(this.throwable);
        } catch (final Throwable throwable) {
          throwable.printStackTrace(); //Print error which could potentially be thrown in the consumer.
        }
      }, async));
  }

  /**
   * @see Response#done()
   */
  @Override
  public boolean done() {
    return this.state.done();
  }

  /**
   * @see Response#canceled()
   */
  @Override
  public boolean canceled() {
    return this.state == State.CANCELLED;
  }

  /**
   * @see Response#exceptionally()
   */
  @Override
  public boolean exceptionally() {
    return this.state == State.COMPLETED_EXCEPTIONALLY;
  }

  /**
   * Update responseFunctions array.
   *
   * @param responseFunction to add to array.
   */
  private synchronized void implementExecutor(@NotNull final ResponseFunction<?> responseFunction) {
    if (this.done()) { //Directly run executor if already finished.
      responseFunction.run(this.executorService);
    } else { //Add to run later if response is completed.
      this.responseFunctions = Arrays.copyOf(this.responseFunctions, this.responseFunctions.length + 1);
      this.responseFunctions[this.responseFunctions.length - 1] = responseFunction;
    }
  }

  /**
   * Execute a {@link Runnable} using the local {@link ExecutorService}.
   *
   * @param runnable to execute.
   */
  private void execute(@NotNull final Runnable runnable) {
    this.executorService.execute(runnable);
  }

  /*
   * --------------------------- Methods and classes for CompletableResponse instance --------------------------------
   */

  /**
   * This record will be filled with a state and information from a {@link CompletableResponse}.
   *
   * @param state     complete {@link State} of response.
   * @param type      if response was completed with a present object. Could be null if state is not {@link State#COMPLETED_DEFAULT}.
   * @param throwable if response was completed exceptionally, present. Null if no error given.
   * @param <TYPE>
   */
  public record Result<TYPE>(@NotNull State state,
                             @Nullable TYPE type,
                             @Nullable Throwable throwable) {
  }

  /**
   * Thread save wrap.
   *
   * @param <TYPE>
   */
  private static class ResultCollection<TYPE> {
    /**
     * Array to hold results.
     */
    private volatile @NotNull Result<TYPE>[] results;

    /**
     * Standard constructor to set results array.
     */
    @SuppressWarnings("unchecked")
    public ResultCollection() {
      this.results = (Result<TYPE>[]) new Result[0];
    }

    /**
     * Add generic {@link Result} to array, use values of {@link CompletableResponse}.
     *
     * @param state     linked to {@link CompletableResponse#state}.
     * @param type      linked to {@link CompletableResponse#response}.
     * @param throwable linked to {@link CompletableResponse#throwable}.
     */
    private synchronized void append(@NotNull State state,
                                     @Nullable TYPE type,
                                     @Nullable Throwable throwable) {
      this.results = Arrays.copyOf(this.results, this.results.length + 1); //Create new array with more space.
      this.results[this.results.length - 1] = new Result<>(state, type, throwable); //Add new Response with new states.
    }

    /**
     * Append any object type to {@link ResultCollection}.
     *
     * @throws ClassCastException if given type is not valid to cast to TYPE.
     * @see ResultCollection#append(State, Object, Throwable)
     */
    @SuppressWarnings("unchecked")
    private synchronized void appendObject(@NotNull State state,
                                           @Nullable Object type,
                                           @Nullable Throwable throwable) {
      this.append(state, (TYPE) type, throwable); //Call append method with generic type.
    }
  }

  /**
   *
   */
  @FunctionalInterface
  public interface EqualFunction {
    /**
     * Method to compare if two objects are the same using {@link Object#equals(Object)}.
     */
    @NotNull EqualFunction EQUALS = (o, obj) -> o != null /*Check if first object is present.*/ && o.equals(obj);
    /**
     * Method to compare the hashCode of the given objects.
     */
    @NotNull EqualFunction HASHCODE = (o, obj) -> o != null && obj != null && o.hashCode() == obj.hashCode();

    /**
     * Method to compare to objects with each-other.
     *
     * @param object1 first object to compare.
     * @param object2 second object to compare with object1.
     * @return true, if both objects are the same.
     */
    boolean equals(@Nullable final Object object1,
                   @Nullable final Object object2);
  }

  /*
   * --------------------------- static methods --------------------------------
   */

  /**
   * Complete a new {@link CompletableResponse} instance with an {@link Throwable}.
   *
   * @param throwable to complete instance with. Also, null possible, if no throwable is given.
   * @param <TYPE>    type of response.
   * @return created {@link CompletableResponse} instance with completed throwable.
   */
  public static @NotNull <TYPE> CompletableResponse<TYPE> exceptionally(@Nullable final Throwable throwable) {
    return new CompletableResponse<TYPE>().completeExceptionally(throwable);
  }

  /**
   * Collect all responses of the specified {@link CompletableResponse} instances.
   * If an answer is null, a null pointer is given as an answer at that position.
   * The type of each instance does not matter in this method.
   *
   * @param responseArray of which the responses are to be collected.
   * @return a new {@link CompletableResponse} instance with {@link ResultCollection} which collects the responses.
   * @see CompletableResponse#collectImplementation(CompletableResponse[])
   */
  public static @NotNull CompletableResponse<Result<Object>[]> collect(@Nullable final CompletableResponse<?>... responseArray) {
    return collectImplementation(toObjectArray(responseArray));
  }

  /**
   * Collect all responses of the specified {@link CompletableResponse} instances.
   * If an answer is null, a null pointer is given as an answer at that position.
   * The type of each instance does not matter in this method.
   *
   * @param responseCollection of which the responses are to be collected.
   * @return a new {@link CompletableResponse} instance with {@link ResultCollection} which collects the responses.
   * @see CompletableResponse#collectImplementation(CompletableResponse[])
   */
  @SuppressWarnings("unchecked")
  public static @NotNull CompletableResponse<Result<Object>[]> collect(@Nullable final Collection<CompletableResponse<?>> responseCollection) {
    return collectImplementation(SpaceObjects.throwIfNull(responseCollection) //Check if collection is present.
      .toArray(new CompletableResponse[0])); //Convert to array for thread safe.
  }

  /**
   * Collect all responses of the specified {@link CompletableResponse} instances.
   * If an answer is null, a null pointer is given as an answer at that position.
   * The type of each instance does not matter in this method.
   * <br>
   * In this variant, all {@link CompletableResponse} must have the same type.
   * The advantage is that the answer then also has a uniform type.
   *
   * @param responseArray of which the responses are to be collected.
   * @param <TYPE>        type of all {@link CompletableResponse} given and return value.
   * @return a new {@link CompletableResponse} instance with {@link ResultCollection} which collects the responses.
   * @see CompletableResponse#collectImplementation(CompletableResponse[])
   */
  @SafeVarargs
  public static @NotNull <TYPE> CompletableResponse<Result<TYPE>[]> collectType(@Nullable final CompletableResponse<TYPE>... responseArray) {
    return collectImplementation(responseArray);
  }

  /**
   * Collect all responses of the specified {@link CompletableResponse} instances.
   * If an answer is null, a null pointer is given as an answer at that position.
   * The type of each instance does not matter in this method.
   * <br>
   * In this variant, all {@link CompletableResponse} must have the same type.
   * The advantage is that the answer then also has a uniform type.
   *
   * @param responseCollection of which the responses are to be collected.
   * @param <TYPE>             type of all {@link CompletableResponse} given and return value.
   * @return a new {@link CompletableResponse} instance with {@link ResultCollection} which collects the responses.
   * @see CompletableResponse#collectImplementation(CompletableResponse[])
   */
  public static @NotNull <TYPE> CompletableResponse<Result<TYPE>[]> collectType(@Nullable final Collection<CompletableResponse<TYPE>> responseCollection) {
    return collectImplementation(collectionToArray(responseCollection));
  }

  /**
   * Implementation for:
   * <ul>
   *   <li>{@link CompletableResponse#collect(CompletableResponse[])}</li>
   *   <li>{@link CompletableResponse#collect(Collection)}</li>
   *   <li>{@link CompletableResponse#collectType(CompletableResponse[])} </li>
   *   <li>{@link CompletableResponse#collectType(Collection)} </li>
   * </ul>
   *
   * @param responseArray to collect responses from and combine into {@link ResultCollection}.
   * @param <TYPE>        type of result to process.
   * @return new instance of {@link CompletableResponse} that will be completed with the {@link ResultCollection} once finished.
   */
  private static @NotNull <TYPE> CompletableResponse<Result<TYPE>[]> collectImplementation(@Nullable final CompletableResponse<TYPE>[] responseArray) {
    SpaceObjects.throwIfNull(responseArray); //Throw error if responseArray is null.

    final CompletableResponse<Result<TYPE>[]> completableResponse = new CompletableResponse<>(); //Create new Response.

    completableResponse.execute(() -> { //Run in of thread.
      final ResultCollection<TYPE> resultCollection = new ResultCollection<>(); //Create resultCollection.

      for (final CompletableResponse<TYPE> response : responseArray) { //Loop trough every response.
        if (response == null) { //Complete with nullPointerException if response is null.
          resultCollection.appendObject(State.UNCOMPLETED, null, new NullPointerException("Response is null!"));
          continue; //Goto next response of array.
        }
        response.sniff(resultCollection::appendObject); //sniff into response and append response to resultCollection.
      }

      while (resultCollection.results.length < responseArray.length) { //Interrupt thread until a response is given.
        if (completableResponse.done()) { //If this response is canceled, interrupt thread.
          return;
        }
      }

      completableResponse.complete(resultCollection.results); //Complete response with collection if all responses where made.
    });

    return completableResponse;
  }

  /**
   * All given {@link CompletableResponse} must be completed with the same value.
   * If this is the case, the returning {@link CompletableResponse} is completed with the equal value.
   * <br>
   * If a value is not present or an error is processed, the response is a {@link MismatchException}.
   *
   * @param responseArray compare elements with each-other.
   * @return response which holds the equal value, or an {@link Throwable}. The given response is never completed with null.
   * @see CompletableResponse#equalImplementation(EqualFunction, CompletableResponse[])
   */
  public static @NotNull CompletableResponse<Object> equal(@Nullable final CompletableResponse<?>... responseArray) {
    return equalImplementation(null, toObjectArray(responseArray));
  }

  /**
   * All given {@link CompletableResponse} must be completed with the same value.
   * If this is the case, the returning {@link CompletableResponse} is completed with the equal value.
   * <br>
   * If a value is not present or an error is processed, the response is a {@link MismatchException}.
   *
   * @param responseCollection compare elements with each-other.
   * @return response which holds the equal value, or an {@link Throwable}. The given response is never completed with null.
   * @see CompletableResponse#equalImplementation(EqualFunction, CompletableResponse[])
   */
  public static @NotNull CompletableResponse<Object> equal(@Nullable final Collection<CompletableResponse<?>> responseCollection) {
    return equal(null, responseCollection);

  }

  /**
   * All given {@link CompletableResponse} must be completed with the same value.
   * If this is the case, the returning {@link CompletableResponse} is completed with the equal value.
   * <br>
   * If a value is not present or an error is processed, the response is a {@link MismatchException}.
   *
   * @param equalFunction function to compare components of responseArray.
   * @param responseArray compare elements with each-other.
   * @return response which holds the equal value, or an {@link Throwable}. The given response is never completed with null.
   * @see CompletableResponse#equalImplementation(EqualFunction, CompletableResponse[])
   */
  public static @NotNull CompletableResponse<Object> equal(@Nullable EqualFunction equalFunction,
                                                           @Nullable final CompletableResponse<?>... responseArray) {
    return equalImplementation(equalFunction, toObjectArray(responseArray));
  }

  /**
   * @param equalFunction      function to compare components of responseCollection.
   * @param responseCollection compare elements with each-other.
   * @return response which holds the equal value, or an {@link Throwable}. The given response is never completed with null.
   * @see CompletableResponse#equalImplementation(EqualFunction, CompletableResponse[])
   */
  @SuppressWarnings("unchecked")
  public static @NotNull CompletableResponse<Object> equal(@Nullable EqualFunction equalFunction,
                                                           @Nullable final Collection<CompletableResponse<?>> responseCollection) {
    return equalImplementation(equalFunction, SpaceObjects.throwIfNull(responseCollection) //Check if collection is present.
      .toArray(new CompletableResponse[0]));
  }

  /**
   * All given {@link CompletableResponse} must be completed with the same value.
   * If this is the case, the returning {@link CompletableResponse} is completed with the equal value.
   * <br>
   * If a value is not present or an error is processed, the response is a {@link MismatchException}.
   * <br>
   * In this variant, all {@link CompletableResponse} must have the same type.
   * The advantage is that the answer then also has a uniform type.
   *
   * @param responseArray compare elements with each-other.
   * @param <TYPE>        type of all {@link CompletableResponse} given and return value.
   * @return response which holds the equal value, or an {@link Throwable}. The given response is never completed with null.
   * @see CompletableResponse#equalImplementation(EqualFunction, CompletableResponse[])
   */
  @SafeVarargs
  public static @NotNull <TYPE> CompletableResponse<TYPE> equalType(@Nullable final CompletableResponse<TYPE>... responseArray) {
    return equalType(null, responseArray);
  }

  /**
   * All given {@link CompletableResponse} must be completed with the same value.
   * If this is the case, the returning {@link CompletableResponse} is completed with the equal value.
   * <br>
   * If a value is not present or an error is processed, the response is a {@link MismatchException}.
   * <br>
   * In this variant, all {@link CompletableResponse} must have the same type.
   * The advantage is that the answer then also has a uniform type.
   *
   * @param responseCollection compare elements with each-other.
   * @param <TYPE>             type of all {@link CompletableResponse} given and return value.
   * @return response which holds the equal value, or an {@link Throwable}. The given response is never completed with null.
   * @see CompletableResponse#equalImplementation(EqualFunction, CompletableResponse[])
   */
  public static @NotNull <TYPE> CompletableResponse<TYPE> equalType(@Nullable final Collection<CompletableResponse<TYPE>> responseCollection) {
    return equalType(null, responseCollection);

  }

  /**
   * All given {@link CompletableResponse} must be completed with the same value.
   * If this is the case, the returning {@link CompletableResponse} is completed with the equal value.
   * <br>
   * If a value is not present or an error is processed, the response is a {@link MismatchException}.
   * <br>
   * In this variant, all {@link CompletableResponse} must have the same type.
   * The advantage is that the answer then also has a uniform type.
   *
   * @param equalFunction function to compare components of responseArray.
   * @param responseArray compare elements with each-other.
   * @param <TYPE>        type of all {@link CompletableResponse} given and return value.
   * @return response which holds the equal value, or an {@link Throwable}. The given response is never completed with null.
   * @see CompletableResponse#equalImplementation(EqualFunction, CompletableResponse[])
   */
  @SafeVarargs
  public static @NotNull <TYPE> CompletableResponse<TYPE> equalType(@Nullable EqualFunction equalFunction,
                                                                    @Nullable final CompletableResponse<TYPE>... responseArray) {
    return equalImplementation(equalFunction, responseArray);
  }

  /**
   * All given {@link CompletableResponse} must be completed with the same value.
   * If this is the case, the returning {@link CompletableResponse} is completed with the equal value.
   * <br>
   * If a value is not present or an error is processed, the response is a {@link MismatchException}.
   * <br>
   * In this variant, all {@link CompletableResponse} must have the same type.
   * The advantage is that the answer then also has a uniform type.
   *
   * @param equalFunction      function to compare components of responseCollection.
   * @param responseCollection compare elements with each-other.
   * @param <TYPE>             type of all {@link CompletableResponse} given and return value.
   * @return response which holds the equal value, or an {@link Throwable}. The given response is never completed with null.
   * @see CompletableResponse#equalImplementation(EqualFunction, CompletableResponse[])
   */
  public static @NotNull <TYPE> CompletableResponse<TYPE> equalType(@Nullable EqualFunction equalFunction,
                                                                    @Nullable final Collection<CompletableResponse<TYPE>> responseCollection) {
    return equalImplementation(equalFunction, collectionToArray(responseCollection));
  }

  /**
   * Implementation for:
   * <ul>
   *   <li>{@link CompletableResponse#equal(CompletableResponse[])}</li>
   *   <li>{@link CompletableResponse#equal(Collection)}</li>
   *   <li>{@link CompletableResponse#equal(EqualFunction, CompletableResponse[])}</li>
   *   <li>{@link CompletableResponse#equal(EqualFunction, Collection)}</li>
   *   <li>{@link CompletableResponse#equalType(CompletableResponse[])} </li>
   *   <li>{@link CompletableResponse#equalType(Collection)} </li>
   *   <li>{@link CompletableResponse#equalType(EqualFunction, CompletableResponse[])}</li>
   *   <li>{@link CompletableResponse#equalType(EqualFunction, Collection)}</li>
   * </ul>
   * <p>
   * If equalFunction is null -> {@link EqualFunction#EQUALS} will be used as default.
   *
   * @param equalFunction function to compare components of responseArray.
   * @param responseArray to compare responses from.
   * @param <TYPE>        type of result to process.
   * @return new instance with processed value. If all responses are the same -> value will be the response of return {@link CompletableResponse}.
   */
  @SafeVarargs
  private static @NotNull <TYPE> CompletableResponse<TYPE> equalImplementation(@Nullable EqualFunction equalFunction,
                                                                               @Nullable final CompletableResponse<TYPE>... responseArray) {
    final EqualFunction finalFunction = equalFunction == null ? EqualFunction.EQUALS : equalFunction; //Define function to compare objects.
    final CompletableResponse<TYPE> completableResponse = new CompletableResponse<>(); //Return value of method

    collectImplementation(responseArray)
      .ifPresentAsync(results -> { //Execute if present.
        if (results.length == 0) { //Return if empty
          completableResponse.completeExceptionally(new MismatchException("No response."));
          return;
        }

        final TYPE compareInstance = results[0].type; //Get first object to compare with next

        for (int i = 1; i < results.length; i++) {
          if (!finalFunction.equals(compareInstance, results[i].type)) {
            completableResponse.completeExceptionally(new MismatchException());
            return;
          }
        }

        completableResponse.complete(compareInstance);
      })
      .ifAbsent(() -> //Complete response with NullPointerException -> No collected response present.
        completableResponse.completeExceptionally(new MismatchException("Response results are empty.")))
      .ifExceptionally(completableResponse::completeExceptionally /*Complete with error.*/);

    return completableResponse;
  }

  /**
   * The first valid response will be used. ({@link CompletableResponse#response} is present.)
   * <br>
   * Thrown error messages are posted to the console.
   *
   * @param responseArray to race each others for the fastest response.
   * @return new instance of {@link CompletableResponse} with that will be completed with the fastest value.
   * @throws NullPointerException if responses is null.
   * @see CompletableResponse#firstImplementation(CompletableResponse[])
   */
  public static @NotNull CompletableResponse<?> first(@Nullable final CompletableResponse<?>... responseArray) {
    return firstImplementation(toObjectArray(responseArray)); //Convert to array for thread safe.
  }

  /**
   * The first valid response will be used. ({@link CompletableResponse#response} is present.)
   * <br>
   * Thrown error messages are posted to the console.
   *
   * @param responseCollection to race each others for the fastest response.
   * @return new instance of {@link CompletableResponse} with that will be completed with the fastest value.
   * @throws NullPointerException if collection is null.
   * @see CompletableResponse#firstImplementation(CompletableResponse[])
   */
  public static @NotNull CompletableResponse<?> first(@Nullable final Collection<CompletableResponse<?>> responseCollection) {
    return first(SpaceObjects.throwIfNull(responseCollection) //Check if collection is present.
      .toArray(new CompletableResponse[0])); //Convert to array for thread safe.
  }

  /**
   * The first valid response will be used. ({@link CompletableResponse#response} is present.)
   * <br>
   * Thrown error messages are posted to the console.
   * <br>
   * In this variant, all {@link CompletableResponse} must have the same type.
   * The advantage is that the answer then also has a uniform type.
   *
   * @param responseArray to race each others for the fastest response.
   * @param <TYPE>        type of the object of every response.
   * @return new instance of {@link CompletableResponse} with that will be completed with the fastest value.
   * @see CompletableResponse#firstImplementation(CompletableResponse[])
   */
  @SafeVarargs
  public static @NotNull <TYPE> CompletableResponse<TYPE> firstType(@Nullable final CompletableResponse<TYPE>... responseArray) {
    return firstImplementation(responseArray);
  }

  /**
   * The first valid response will be used. ({@link CompletableResponse#response} is present.)
   * <br>
   * Thrown error messages are posted to the console.
   * <br>
   * In this variant, all {@link CompletableResponse} must have the same type.
   * The advantage is that the answer then also has a uniform type.
   *
   * @param responseCollection to race each others for the fastest response.
   * @param <TYPE>             type of the object of every response.
   * @return new instance of {@link CompletableResponse} with that will be completed with the fastest value.
   * @see CompletableResponse#firstImplementation(CompletableResponse[])
   */
  public static @NotNull <TYPE> CompletableResponse<TYPE> firstType(@Nullable final Collection<CompletableResponse<TYPE>> responseCollection) {
    return firstImplementation(collectionToArray(responseCollection));
  }

  /**
   * Implementation for:
   * <ul>
   *   <li>{@link CompletableResponse#first(CompletableResponse[])}</li>
   *   <li>{@link CompletableResponse#first(Collection)}</li>
   *   <li>{@link CompletableResponse#firstType(CompletableResponse[])} </li>
   *   <li>{@link CompletableResponse#firstType(Collection)} </li>
   * </ul>
   *
   * @param responseArray to race each others for the fastest response.
   * @param <TYPE>        type of the object of every response.
   * @return new instance of {@link CompletableResponse} with that will be completed with the fastest value or an error if no response can find a value.
   */
  @SafeVarargs
  private static @NotNull <TYPE> CompletableResponse<TYPE> firstImplementation(@Nullable final CompletableResponse<TYPE>... responseArray) {
    SpaceObjects.throwIfNull(responseArray, "Given array is null."); //Throw error if responses is null.

    final CompletableResponse<TYPE> completableResponse = new CompletableResponse<>(); //Create new response -> return value of this method.

    completableResponse.execute(() -> {
      final AtomicInteger atomicInteger = new AtomicInteger(0); //Count completed requests.

      for (final CompletableResponse<TYPE> response : responseArray) { //Loop trough every component of list.
        if (response == null) { //Ignore response if null.
          atomicInteger.incrementAndGet(); //Null responses also count as response done.
          continue;
        }

        response
          .ifPresent(completableResponse::complete /*Complete response with supplied response.*/)
          .ifExceptionally(SpaceThrowable::printStackTrace /*Print throwable stacktrace if present.*/)
          .run(atomicInteger::incrementAndGet /*Increment counter to validate given responses.*/);
      }

      while (atomicInteger.get() < responseArray.length) { //Interrupt thread until a response is given.
        if (completableResponse.done()) { //If response is done or canceled.
          return;
        }
      }

      completableResponse.completeExceptionally(new NullPointerException("No valid response present.")); //Complete with error, no present response was given.
    });
    return completableResponse;
  }

  /*
   * --------------------------- Private methods for static methods of this class --------------------------------
   */

  /**
   * Convert an array of different response types to {@link CompletableResponse} with object as type.
   *
   * @param responseArray to convert.
   * @return new array instance with object instances.
   * @throws NullPointerException if responseArray is null.
   */
  @SuppressWarnings("unchecked")
  private static CompletableResponse<Object>[] toObjectArray(@Nullable final CompletableResponse<?>... responseArray) {
    final CompletableResponse<Object>[] objectArray = (CompletableResponse<Object>[])
      new CompletableResponse[SpaceObjects.throwIfNull(responseArray) /*Check if responseArray is present and not null*/.length]; //Create new array with object responses.
    for (int i = 0; i < responseArray.length; i++) { //Loop trough every index of responseArray
      objectArray[i] = (CompletableResponse<Object>) responseArray[i]; //Add every response from responseArray to new created array.
    }
    return objectArray;
  }

  /**
   * Convert an {@link java.util.Collection} with a given TYPE to an array of that exact TYPE.
   *
   * @param <TYPE> type of the collection.
   * @return array with elements of collection.
   * @throws NullPointerException if collection is null.
   */
  @SuppressWarnings("unchecked")
  private static @NotNull <TYPE> CompletableResponse<TYPE>[] collectionToArray(@Nullable final Collection<CompletableResponse<TYPE>> collection) {
    return SpaceObjects.throwIfNull(collection) //Check if collection is present.
      .toArray((CompletableResponse<TYPE>[]) new CompletableResponse[0]);
  }
}
package dev.dotspace.common.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.dotspace.common.exception.EmptyArrayException;

import java.util.Random;
import java.util.function.Predicate;

public interface Manager<WRAPPER extends Wrapper> {

  /**
   * If wrapper is null or storageType is null, method will ignore call.
   *
   * @param wrapper
   * @param wrapperType
   * @return
   */
  @NotNull Manager<WRAPPER> register(@Nullable final WRAPPER wrapper,
                                     @Nullable final WrapperType wrapperType);

  /**
   * If wrapper is null, method will ignore call.
   *
   * @param wrapper
   * @return
   */
  @NotNull Manager<WRAPPER> unregister(@Nullable final Wrapper wrapper);

  @NotNull Manager<WRAPPER> unregisterAll();


  @NotNull WRAPPER[] wrappers();

  /**
   * @param wrapperType
   * @return
   * @throws NullPointerException if storageType is null.
   */
  @NotNull WRAPPER[] wrappers(@Nullable final WrapperType wrapperType);

  /**
   * @return
   * @throws EmptyArrayException if {@link Manager#wrappers()} is empty.
   */
  @NotNull Query<WRAPPER> query();


  /**
   * @return
   * @throws EmptyArrayException  if {@link Manager#wrappers(WrapperType)} are empty.
   * @throws NullPointerException if storageType is null.
   */
  @NotNull Query<WRAPPER> query(@Nullable final WrapperType wrapperType);
}

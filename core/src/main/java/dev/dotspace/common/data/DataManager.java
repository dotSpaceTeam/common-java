package dev.dotspace.common.data;

import dev.dotspace.common.SpaceArrays;
import dev.dotspace.common.SpaceObjects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public final class DataManager<WRAPPER extends Wrapper> implements Manager<WRAPPER> {

  private volatile Pair<WRAPPER>[] wrapperPairs;

  public DataManager() {
    this.wrapperPairs = (Pair<WRAPPER>[]) new Pair[0];
  }

  @Override
  public synchronized @NotNull Manager<WRAPPER> register(@Nullable WRAPPER wrapper,
                                                         @Nullable WrapperType wrapperType) {
    if (wrapper != null && wrapperType != null) {
      this.wrapperPairs = SpaceArrays.push(this.wrapperPairs, new Pair<>(wrapperType, wrapper));
    }
    return this;
  }

  @Override
  public synchronized @NotNull Manager<WRAPPER> unregister(@Nullable Wrapper wrapper) {

    return this;
  }

  @Override
  public synchronized @NotNull Manager<WRAPPER> unregisterAll() {

    Arrays.fill(this.wrapperPairs, new Wrapper[0]);
    return this;
  }

  @Override
  public synchronized @NotNull WRAPPER[] wrappers() {
    return this.wrappersImplementation().wrappers();
  }

  /**
   * @see Manager#wrappers(WrapperType).
   */
  @Override
  public @NotNull WRAPPER[] wrappers(@Nullable WrapperType wrapperType) {
    return this.wrappersImplementation(wrapperType).wrappers();
  }

  @Override
  public @NotNull Query<WRAPPER> query() {
    final WrapperList<WRAPPER> wrapperList = this.wrappersImplementation();

    return new DataQuery<>(wrapperList.wrappers(), wrapperList.listType());
  }

  @Override
  public @NotNull Query<WRAPPER> query(@Nullable WrapperType wrapperType) {
    SpaceObjects.throwIfNull(wrapperType);

    return new DataQuery<>(this.wrappersImplementation(wrapperType).wrappers(), wrapperType);
  }


  private @NotNull WrapperList<WRAPPER> wrappersImplementation() {
    WRAPPER[] wrappers = (WRAPPER[]) new Wrapper[0];
    WrapperType wrapperType = WrapperType.PERSISTENT;

    for (final Pair<WRAPPER> wrapperPair : this.wrapperPairs) {
      wrappers = SpaceArrays.push(wrappers, wrapperPair.wrapper());

      if (wrapperPair.wrapperType() != WrapperType.PERSISTENT) {
        wrapperType = WrapperType.VOLATILE;
      }
    }
    return new WrapperList<>(wrappers, wrapperType);
  }

  private @NotNull WrapperList<WRAPPER> wrappersImplementation(@Nullable WrapperType wrapperType) {
    SpaceObjects.throwIfNull(wrapperType);

    WRAPPER[] wrappers = (WRAPPER[]) new Wrapper[0];

    for (final Pair<WRAPPER> wrapper : this.wrapperPairs) {
      if (wrapper.wrapperType() != wrapperType) {
        continue;
      }
      wrappers = SpaceArrays.push(wrappers, wrapper.wrapper());
    }
    return new WrapperList<>(wrappers, wrapperType);
  }

  /**
   * static
   */
  public static @NotNull <WRAPPER extends Wrapper> DataManager<WRAPPER> create() {
    return new DataManager<>();
  }

  public static @NotNull <WRAPPER extends Wrapper> DataManager<WRAPPER> create(@Nullable final Class<WRAPPER> wrapperClass) {
    return new DataManager<>();
  }

  private record Pair<WRAPPER extends Wrapper>(@NotNull WrapperType wrapperType,
                                               @NotNull WRAPPER wrapper) {
  }

  /**
   * @param wrappers
   * @param listType  [{@link WrapperType#PERSISTENT} if all wrappers are this type.]
   * @param <WRAPPER>
   */
  private record WrapperList<WRAPPER extends Wrapper>(@NotNull WRAPPER[] wrappers,
                                                      @NotNull WrapperType listType) {

  }
}

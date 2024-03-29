package dev.dotspace.common;

import dev.dotspace.common.annotation.JUnitVerification;
import dev.dotspace.common.annotation.LibraryInformation;
import dev.dotspace.common.response.CompletableResponse;
import dev.dotspace.common.exception.EmptyArrayException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Methods with which arrays are handled. The individual methods are directly commented.
 */
@SuppressWarnings("unused")
@LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
public final class SpaceArrays {
  /**
   * Block default constructor.
   */
  private SpaceArrays() {
    //Nothing to see here.
  }

  /**
   * Get a random element of array of any type.
   *
   * @param array  to get random element of.
   * @param <TYPE> generic type of element to get random.
   * @return random drawn element or null if array is null or empty.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static <TYPE> @Nullable TYPE random(@Nullable final TYPE[] array) {
    if (SpaceObjects.throwIfNull(array).length == 0) {
      throw new EmptyArrayException("Given object array is null.");
    }
    return array[LibraryCommonUtils.calculateRandomIndex(array.length)];
  }

  /**
   * @param array  to get random element of.
   * @param <TYPE> generic type of element to get random.
   * @return random element as {@link CompletableResponse}.
   * @see CompletableResponse
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static <TYPE> @NotNull CompletableResponse<TYPE> randomAsync(@Nullable final TYPE[] array) {
    return new CompletableResponse<TYPE>().completeAsync(() -> SpaceArrays.random(array)); //Complete the future in a separate thread
  }

  /**
   * Get a random element from the given array.
   *
   * @param bytes to get random element of.
   * @return random byte of given array.
   * @throws NullPointerException if bytes is null.
   * @throws EmptyArrayException  if bytes is empty.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static byte random(final byte[] bytes) {
    if (SpaceObjects.throwIfNull(bytes).length == 0) {
      throw new EmptyArrayException("Given byte array is empty.");
    }
    return bytes[LibraryCommonUtils.calculateRandomIndex(bytes.length)];
  }

  /**
   * If the array is null, the {@link CompletableResponse} is completed with {@link NullPointerException}. If the
   * array is empty, the answer is completed with {@link EmptyArrayException}.
   *
   * @param bytes to get random element of.
   * @return instance of {@link CompletableResponse} with the random element.
   * @see CompletableResponse
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static @NotNull CompletableResponse<Byte> randomAsync(final byte[] bytes) {
    return new CompletableResponse<Byte>().completeAsync(() -> SpaceArrays.random(bytes));
  }

  /**
   * Get a random element from the given array.
   *
   * @param shorts to get random element of.
   * @return random short of given array.
   * @throws NullPointerException if shorts is null.
   * @throws EmptyArrayException  if shorts is empty.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static short random(final short[] shorts) {
    if (SpaceObjects.throwIfNull(shorts).length == 0) {
      throw new EmptyArrayException("Given short array is empty.");
    }
    return shorts[LibraryCommonUtils.calculateRandomIndex(shorts.length)];
  }

  /**
   * If the array is null, the {@link CompletableResponse} is completed with {@link NullPointerException}. If the
   * array is empty, the answer is completed with {@link EmptyArrayException}.
   *
   * @param shorts to get random element of.
   * @return instance of {@link CompletableResponse} with the random element.
   * @see CompletableResponse
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static @NotNull CompletableResponse<Short> randomAsync(final short[] shorts) {
    return new CompletableResponse<Short>().completeAsync(() -> SpaceArrays.random(shorts));
  }

  /**
   * Get a random element from the given array.
   *
   * @param chars to get random element of.
   * @return random chars of given array.
   * @throws NullPointerException if chars is null.
   * @throws EmptyArrayException  if chars is empty.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static char random(final char[] chars) {
    if (SpaceObjects.throwIfNull(chars).length == 0) {
      throw new EmptyArrayException("Given char array is empty.");
    }
    return chars[LibraryCommonUtils.calculateRandomIndex(chars.length)];
  }

  /**
   * If the array is null, the {@link CompletableResponse} is completed with {@link NullPointerException}. If the
   * array is empty, the answer is completed with {@link EmptyArrayException}.
   *
   * @param chars to get random element of.
   * @return instance of {@link CompletableResponse} with the random element.
   * @see CompletableResponse
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static @NotNull CompletableResponse<Character> randomAsync(final char[] chars) {
    return new CompletableResponse<Character>().completeAsync(() -> SpaceArrays.random(chars));
  }

  /**
   * Get a random element from the given array.
   *
   * @param ints to get random element of.
   * @return random int of given array.
   * @throws NullPointerException if ints is null.
   * @throws EmptyArrayException  if ints is empty.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static int random(final int[] ints) {
    if (SpaceObjects.throwIfNull(ints).length == 0) {
      throw new EmptyArrayException("Given int array is empty.");
    }
    return ints[LibraryCommonUtils.calculateRandomIndex(ints.length)];
  }

  /**
   * If the array is null, the {@link CompletableResponse} is completed with {@link NullPointerException}. If the
   * array is empty, the answer is completed with {@link EmptyArrayException}.
   *
   * @param ints to get random element of.
   * @return instance of {@link CompletableResponse} with the random element.
   * @see CompletableResponse
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static @NotNull CompletableResponse<Integer> randomAsync(final int[] ints) {
    return new CompletableResponse<Integer>().completeAsync(() -> SpaceArrays.random(ints));
  }

  /**
   * Get a random element from the given array.
   *
   * @param longs to get random element of.
   * @return random long of given array.
   * @throws NullPointerException if longs is null.
   * @throws EmptyArrayException  if longs is empty.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static long random(final long[] longs) {
    if (SpaceObjects.throwIfNull(longs).length == 0) {
      throw new EmptyArrayException("Given long array is empty.");
    }
    return longs[LibraryCommonUtils.calculateRandomIndex(longs.length)];
  }

  /**
   * If the array is null, the {@link CompletableResponse} is completed with {@link NullPointerException}. If the
   * array is empty, the answer is completed with {@link EmptyArrayException}.
   *
   * @param longs to get random element of.
   * @return instance of {@link CompletableResponse} with the random element.
   * @see CompletableResponse
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static @NotNull CompletableResponse<Long> randomAsync(final long[] longs) {
    return new CompletableResponse<Long>().completeAsync(() -> SpaceArrays.random(longs));
  }

  /**
   * Get a random element from the given array.
   *
   * @param floats to get random element of.
   * @return random float of given array.
   * @throws NullPointerException if floats is null.
   * @throws EmptyArrayException  if floats is empty.
   */
  public static float random(final float[] floats) {
    if (SpaceObjects.throwIfNull(floats).length == 0) {
      throw new EmptyArrayException("Given float array is empty.");
    }
    return floats[LibraryCommonUtils.calculateRandomIndex(floats.length)];
  }

  /**
   * If the array is null, the {@link CompletableResponse} is completed with {@link NullPointerException}. If the
   * array is empty, the answer is completed with {@link EmptyArrayException}.
   *
   * @param floats to get random element of.
   * @return instance of {@link CompletableResponse} with the random element.
   * @see CompletableResponse
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static @NotNull CompletableResponse<Float> randomAsync(final float[] floats) {
    return new CompletableResponse<Float>().completeAsync(() -> SpaceArrays.random(floats));
  }

  /**
   * Get a random element from the given array.
   *
   * @param doubles to get random element of.
   * @return random double of given array.
   * @throws NullPointerException if doubles is null.
   * @throws EmptyArrayException  if doubles is empty.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static double random(final double[] doubles) {
    if (SpaceObjects.throwIfNull(doubles).length == 0) {
      throw new EmptyArrayException("Given double array is empty.");
    }
    return doubles[LibraryCommonUtils.calculateRandomIndex(doubles.length)];
  }

  /**
   * If the array is null, the {@link CompletableResponse} is completed with {@link NullPointerException}. If the
   * array is empty, the answer is completed with {@link EmptyArrayException}.
   *
   * @param doubles to get random element of.
   * @return instance of {@link CompletableResponse} with the random element.
   * @see CompletableResponse
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.6")
  public static @NotNull CompletableResponse<Double> randomAsync(final double[] doubles) {
    return new CompletableResponse<Double>().completeAsync(() -> SpaceArrays.random(doubles));
  }

  /**
   * Append elements to array.
   *
   * @param typeArray     to append content to.
   * @param typesToAppend to append to typeArray elements -> Creates new array.
   * @param <TYPE>        type of both array elements.
   * @return new array instance with combined elements.
   * @throws NullPointerException if typeArray or typesToAppend is null.
   */
  @LibraryInformation(state = LibraryInformation.State.EXPERIMENTAL, since = "1.0.6")
  @SafeVarargs
  public static <TYPE> @NotNull TYPE[] push(final TYPE @Nullable [] typeArray,
                                            final TYPE @Nullable ... typesToAppend) {
    if (typeArray == null || typesToAppend == null) {
      throw new NullPointerException();
    }

    final TYPE[] newArray = Arrays.copyOf(typeArray, typeArray.length + typesToAppend.length);
    System.arraycopy(typesToAppend, 0, newArray, typeArray.length, typesToAppend.length);
    return newArray;
  }

  /**
   * Append element to array.
   *
   * @param typeArray  to append element typeToAppend.
   * @param typeToPush element to push at the end of typeArray.
   * @param <TYPE>     generic type of elements in array.
   * @return new array with new size and content of typeArray plus typeToAppend.
   * @throws NullPointerException if typeArray is null.
   */
  @LibraryInformation(state = LibraryInformation.State.EXPERIMENTAL, since = "1.0.7")
  @JUnitVerification
  public static <TYPE> @NotNull TYPE[] push(final TYPE @Nullable [] typeArray,
                                            @Nullable final TYPE typeToPush) {
    return SpaceArrays.pushImplementation(SpaceObjects.throwIfNull(typeArray), typeToPush);
  }

  /**
   * Append element to array. Implementation.
   *
   * @param typeArray  to append element.
   * @param typeToPush to append to typeArray.
   * @param <TYPE>     generic type of typeArray and typeToAppend.
   * @return new array instance with pushed object.
   */
  @LibraryInformation(state = LibraryInformation.State.EXPERIMENTAL, since = "1.0.7")
  private static <TYPE> @NotNull TYPE[] pushImplementation(final TYPE @NotNull [] typeArray,
                                                           @Nullable final TYPE typeToPush) {
    final TYPE[] newArray = Arrays.copyOf(typeArray, typeArray.length + 1); //Create new array with one more position.
    newArray[newArray.length - 1] = typeToPush; //Set typeToAppend as last index of new array.
    return newArray;
  }

  /**
   * Remove elements with null reference from array.
   *
   * @param typeArray to drop null elements from.
   * @param <TYPE>    generic type of typeArray and typeToAppend.
   * @return new array instance with removed null elements.
   * @throws NullPointerException if typeArray is null.
   */
  @LibraryInformation(state = LibraryInformation.State.EXPERIMENTAL, since = "1.0.7")
  public static <TYPE> @NotNull TYPE[] dropNull(final TYPE @Nullable [] typeArray) {
    SpaceObjects.throwIfNull(typeArray); //Throw error if array is null.
    TYPE[] array = (TYPE[]) new Object[0];

    for (TYPE type : typeArray) { //Loop trough present array.
      if (type == null) {
        continue;
      }
      array = SpaceArrays.pushImplementation(array, type); //Append present element to copy of array
    }
    return array;
  }

}
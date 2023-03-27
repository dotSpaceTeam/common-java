package dev.dotspace.common.math;


import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;


/**
 * Keep error due to conversion in mind!
 */
@Getter
@Accessors(fluent = true)
public class Vector {
  private final double x;
  private final double y;
  private final double z;

  public Vector(final double x,
                final double y,
                final double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Vector() {
    this(0D, 0D, 0D);
  }

  /**
   * Deep clone this class.
   *
   * @return new instance with same values.
   */
  public @NotNull Vector deepClone() {
    return new Vector(this.x, this.y, this.z);
  }

  public @NotNull Vector x(final double x) {
    return new Vector(x, this.y, this.z);
  }

  public @NotNull Vector y(final double y) {
    return new Vector(this.x, y, this.z);
  }

  public @NotNull Vector z(final double z) {
    return new Vector(this.x, this.y, z);
  }

  /**
   * Add another {@link Vector} from this instance.
   *
   * @param vector to add.
   * @return new Vector instance.
   */
  public @NotNull Vector add(@NotNull final Vector vector) {
    return new Vector(this.x + vector.x(), this.y + vector.y(), this.z + vector.z());
  }

  /**
   * Subtract another {@link Vector} from this instance.
   *
   * @param vector to subtract.
   * @return new Vector instance.
   */
  public @NotNull Vector subtract(@NotNull final Vector vector) {
    return new Vector(this.x - vector.x(), this.y - vector.y(), this.z - vector.z());
  }

  /**
   * Multiply another {@link Vector} from this instance.
   *
   * @param vector to multiply.
   * @return new Vector instance.
   */
  public @NotNull Vector multiply(@NotNull final Vector vector) {
    return new Vector(this.x * vector.x(), this.y * vector.y(), this.z * vector.z());
  }

  /**
   * Divide another {@link Vector} from this instance.
   *
   * @param vector to divide.
   * @return new Vector instance.
   */
  public @NotNull Vector divide(@NotNull final Vector vector) {
    return new Vector(this.x / vector.x(), this.y / vector.y(), this.z / vector.z());
  }

  /**
   * Multiply this {@link Vector}.
   *
   * @param multiply double value to multiply this vector with.
   * @return new Vector instance with changed multiplier.
   */
  public @NotNull Vector multiply(final double multiply) {
    return new Vector(this.x * multiply, this.y * multiply, this.z * multiply);
  }

  /**
   * Multiply this {@link Vector}.
   *
   * @param multiply int value to multiply this vector with.
   * @return new Vector instance with changed multiplier.
   */
  public @NotNull Vector multiply(final int multiply) {
    return this.multiply((double) multiply);
  }

  /**
   * Compare value with instance.
   *
   * @param object to compare with this object.
   * @return true, if object is instanceof {@link Vector} and positions match.
   */
  @Override
  public boolean equals(@NotNull final Object object) {
    if (object instanceof Vector vector && this.getClass().equals(object.getClass())) {
      return Math.abs(this.x() - vector.x()) < 1.0E-6 &&
        Math.abs(this.y() - vector.y()) < 1.0E-6 &&
        Math.abs(this.z() - vector.z()) < 1.0E-6;
    }
    return false;
  }

  /**
   * Calculates the length. [{@link Math#sqrt(double)} applied]
   *
   * @return value as double.
   */
  public double length() {
    return Math.sqrt(lengthSquared());
  }

  /**
   * Calculates the length.
   *
   * @return value as double
   */
  public double lengthSquared() {
    return (this.x * this.x) + (this.y * this.y) + (this.z * this.z);
  }

  public final int xInt() {
    return (int) this.x();
  }

  public final int yInt() {
    return (int) this.y();
  }

  public final int zInt() {
    return (int) this.z();
  }

  //static
  public static @NotNull Vector minimum(@NotNull final Vector vector1,
                                        @NotNull final Vector vector2) {
    return new Vector(Math.min(vector1.x(), vector2.x()),
      Math.min(vector1.y(), vector2.y()),
      Math.min(vector1.z(), vector2.z()));
  }

  public static @NotNull Vector maximum(@NotNull final Vector vector1,
                                        @NotNull final Vector vector2) {
    return new Vector(Math.max(vector1.x(), vector2.x()),
      Math.max(vector1.y(), vector2.y()),
      Math.max(vector1.z(), vector2.z()));
  }
}

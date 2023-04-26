package dev.dotspace.common.math;


import dev.dotspace.common.annotation.JUnitVerification;
import dev.dotspace.common.annotation.LibraryInformation;
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
  @JUnitVerification
  public @NotNull Vector add(@NotNull final Vector vector) {
    return this.add(vector.x(), vector.y(), vector.z());
  }

  /**
   * Add another {@link Vector} from this instance.
   *
   * @param x coordinate to add
   * @param y coordinate to add
   * @param z coordinate to add
   * @return new Vector instance.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  public @NotNull Vector add(final double x, final double y, final double z) {
    return new Vector(this.x + x, this.y + y, this.z + z);
  }

  /**
   * Subtract another {@link Vector} from this instance.
   *
   * @param vector to subtract.
   * @return new Vector instance.
   */
  @JUnitVerification
  public @NotNull Vector subtract(@NotNull final Vector vector) {
    return this.subtract(vector.x(), vector.y(), vector.z());
  }

  /**
   * Subtract another {@link Vector} to this instance.
   *
   * @param x coordinate to subtract
   * @param y coordinate to subtract
   * @param z coordinate to subtract
   * @return new Vector instance.
   */
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  public @NotNull Vector subtract(final double x, final double y, final double z) {
    return new Vector(this.x - x, this.y - y, this.z - z);
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
  @JUnitVerification
  public @NotNull Vector divide(@NotNull final Vector vector) {
    return new Vector(this.x / vector.x(), this.y / vector.y(), this.z / vector.z());
  }

  /**
   * Multiply this {@link Vector}.
   *
   * @param multiply double value to multiply this vector with.
   * @return new Vector instance with changed multiplier.
   */
  @JUnitVerification
  public @NotNull Vector multiply(final double multiply) {
    return new Vector(this.x * multiply, this.y * multiply, this.z * multiply);
  }

  /**
   * Multiply this {@link Vector}.
   *
   * @param multiply int value to multiply this vector with.
   * @return new Vector instance with changed multiplier.
   */
  @JUnitVerification
  public @NotNull Vector multiply(final int multiply) {
    return this.multiply((double) multiply);
  }

  /**
   * Performs scalar multiplication with this {@link Vector}.
   *
   * @param vector to perform multiplication.
   * @return computed scalar product.
   */
  @JUnitVerification
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  public double scalarProduct(final Vector vector) {
    return (this.x * vector.x) + (this.y * vector.y) + (this.z * vector.z);
  }

  /**
   * Calculates the cross product of this {@link Vector}.
   *
   * @param vector to perform calculation with.
   * @return new Vector instance, perpendicular to both input {@link Vector}s.
   */
  @JUnitVerification
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  public Vector crossProduct(final Vector vector) {
    return new Vector(
      this.y * vector.z - this.z * vector.y,
      this.z * vector.x - this.x * vector.z,
      this.x * vector.y - this.y * vector.x
    );
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
   * Calculates the length/magnitude.
   *
   * @return the value as double.
   */
  @JUnitVerification
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

  /**
   * Checks orthogonality of this {@link Vector}.
   *
   * @param vector to check with.
   * @return true, if both {@link Vector}s are right-angled towards each other.
   */
  @JUnitVerification
  @LibraryInformation(state = LibraryInformation.State.STABLE, since = "1.0.8")
  public boolean isOrthogonalTo(final Vector vector) {
    return scalarProduct(vector) == 0;
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

  @Override
  public String toString() {
    return "Vector[" +
      "x=" + this.x +
      "; y=" + this.y +
      "; z=" + this.z +
      ']';
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

package dev.dotspace.common.test.math;

import dev.dotspace.common.math.Vector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Vector} methods.
 * If necessary, Please round the expected value to the 9th decimal.
 */
public class VectorCalculusTest {

  private final Vector[] SOURCE_VECTORS = {
    new Vector(3, 8, -7),
    new Vector(1, -2, 5),
    new Vector(-1.75, 4.5, -8)
  };

  @Test
  public void testAddition() {
    assertEquals(new Vector(6, 16, -14), SOURCE_VECTORS[0].add(SOURCE_VECTORS[0]));
    assertEquals(new Vector(4, 6, -2), SOURCE_VECTORS[0].add(SOURCE_VECTORS[1]));
    assertEquals(new Vector(1.25, 12.5, -15), SOURCE_VECTORS[0].add(SOURCE_VECTORS[2]));
  }

  @Test
  public void testSubtraction() {
    assertEquals(new Vector(), SOURCE_VECTORS[0].subtract(SOURCE_VECTORS[0]));
    assertEquals(new Vector(2, 10, -12), SOURCE_VECTORS[0].subtract(SOURCE_VECTORS[1]));
    assertEquals(new Vector(4.75, 3.5, 1), SOURCE_VECTORS[0].subtract(SOURCE_VECTORS[2]));
  }

  @Test
  public void testMultiplication() {
    assertEquals(SOURCE_VECTORS[0], SOURCE_VECTORS[0].multiply(1));
    assertEquals(new Vector(-3, -8, 7), SOURCE_VECTORS[0].multiply(-1));
    assertEquals(new Vector(-6.75, -18, 15.75), SOURCE_VECTORS[0].multiply(-2.25));
    assertEquals(new Vector(6.75, 18, -15.75), SOURCE_VECTORS[0].multiply(2.25));
  }

  @Test
  public void testDivision() {
    assertEquals(new Vector(1, 1, 1), SOURCE_VECTORS[0].divide(SOURCE_VECTORS[0]));
    assertEquals(new Vector(3, -4, -1.4), SOURCE_VECTORS[0].divide(SOURCE_VECTORS[1]));
    assertEquals(new Vector(-1.714285714, 1.777777778, 0.875), SOURCE_VECTORS[0].divide(SOURCE_VECTORS[2]));
  }

  @Test
  public void testScalarProduct() {
    assertEquals(122, SOURCE_VECTORS[0].scalarProduct(SOURCE_VECTORS[0]));
    assertEquals(-48, SOURCE_VECTORS[0].scalarProduct(SOURCE_VECTORS[1]));
    assertEquals(86.75, SOURCE_VECTORS[0].scalarProduct(SOURCE_VECTORS[2]));
  }

  @Test
  public void testCrossProduct() {
    assertEquals(new Vector(), SOURCE_VECTORS[0].crossProduct(SOURCE_VECTORS[0]));
    assertEquals(new Vector(26,-22,-14), SOURCE_VECTORS[0].crossProduct(SOURCE_VECTORS[1]));
    assertEquals(new Vector(-32.5,36.25,27.5), SOURCE_VECTORS[0].crossProduct(SOURCE_VECTORS[2]));
  }

  @Test
  public void testLength() {
    assertEquals(Math.sqrt(122), SOURCE_VECTORS[0].length());
    assertEquals(Math.sqrt(30), SOURCE_VECTORS[1].length());
    assertTrue(Math.abs(SOURCE_VECTORS[2].length() - 9.344115796)< 1.0E-6);
    assertEquals(0, SOURCE_VECTORS[0].crossProduct(SOURCE_VECTORS[0]).length());
    assertEquals(2*Math.sqrt(339), SOURCE_VECTORS[0].crossProduct(SOURCE_VECTORS[1]).length());
    assertTrue(Math.abs(SOURCE_VECTORS[0].crossProduct(SOURCE_VECTORS[2]).length() - 55.91567312)< 1.0E-6);
  }

  @Test
  public void testOrthogonality() {
    assertFalse(SOURCE_VECTORS[0].isOrthogonalTo(SOURCE_VECTORS[1]));
    assertFalse(SOURCE_VECTORS[1].isOrthogonalTo(SOURCE_VECTORS[0]));
    assertTrue(SOURCE_VECTORS[1].isOrthogonalTo(new Vector()));
    assertTrue(SOURCE_VECTORS[0].isOrthogonalTo(new Vector(-32.5,36.25,27.5)));
    assertTrue(SOURCE_VECTORS[2].isOrthogonalTo(new Vector(-32.5,36.25,27.5)));
  }
}

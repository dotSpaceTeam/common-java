package dev.dotspace.common.test;

import dev.dotspace.common.ObjectLabel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test Class {@link dev.dotspace.common.ObjectLabel}.
 */
public class ObjectLabelTest {

  @Test
  public void testGetter() {
    final short label = 1;
    final String object = "test";
    final ObjectLabel<Short, String> objectLabel = new ObjectLabel<>(label, object);

    Assertions.assertEquals(objectLabel.label(), label);
    Assertions.assertEquals(objectLabel.object(), object);
  }

  @Test
  public void testHashCode() {
    final String label = "testLabel";
    final Map<String, Integer> object = new HashMap<>();

    Assertions.assertEquals(object.hashCode(), new ObjectLabel<>(label, object).hashCode());
  }
}

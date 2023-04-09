package dev.dotspace.common.data;

public enum WrapperType {

  PERSISTENT(0),
  VOLATILE(1);

  private final int index;

  WrapperType(final int index) {
    this.index = index;
  }

  public int index() {
    return this.index;
  }
}

package dev.dotspace.common.concurrent;

public enum ProcessType {
  /**
   * Used for single thread.
   */
  SINGLE(false),
  /**
   * Used for multi thread operations.
   */
  MULTI(true);

  private final boolean multiThread;

  private ProcessType(boolean multiThread) {
    this.multiThread = multiThread;
  }

  public boolean multiThread() {
    return this.multiThread;
  }
}

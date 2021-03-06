package com.vesperin.cue.spi;

/**
 * @author Huascar Sanchez
 */
public class CycleEdgesException extends RuntimeException {
  /**
   * Constructs a new CycleEdgesException object with a
   * message.
   *
   * @param message human readable message.
   */
  public CycleEdgesException(String message) {
    super(message);
  }
}

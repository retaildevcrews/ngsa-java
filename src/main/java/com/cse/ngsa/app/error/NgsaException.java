package com.cse.ngsa.app.error;

/** Custom Exception to address linter feedback. */
public class NgsaException extends Exception {
  private static final long serialVersionUID = -1905031427519507137L;

  public NgsaException() {
  }

  public NgsaException(String message) {
    super(message);
  }
}

package com.antra.evaluation.reporting_system.exception;

public class ExcelFileCreationException extends RuntimeException {
  public ExcelFileCreationException(String message, Throwable t) {
    super(message, t);
  }
}

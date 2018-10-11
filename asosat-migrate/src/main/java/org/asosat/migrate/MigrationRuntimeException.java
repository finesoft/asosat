package org.asosat.migrate;

import org.asosat.kernel.exception.KernelRuntimeException;

public class MigrationRuntimeException extends KernelRuntimeException {

  private static final long serialVersionUID = 7832289179177838540L;

  public MigrationRuntimeException() {
    super();
  }

  public MigrationRuntimeException(String message) {
    super(message);
  }

  public MigrationRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  public MigrationRuntimeException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public MigrationRuntimeException(Throwable cause) {
    super(cause);
  }


}

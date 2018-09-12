/*
 * Copyright (c) 2013-2018. BIN.CHEN
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.asosat.kernel.exception;

/**
 * asosat-kernel
 *
 * @author bingo 上午9:47:57
 *
 */
public class KernelRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 4872149093400648113L;

  public KernelRuntimeException() {
    super();
  }

  /**
   * @param message
   */
  public KernelRuntimeException(String message) {
    super(message);
  }

  /**
   * @param message
   * @param cause
   */
  public KernelRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public KernelRuntimeException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  /**
   * @param cause
   */
  public KernelRuntimeException(Throwable cause) {
    super(cause);
  }

}

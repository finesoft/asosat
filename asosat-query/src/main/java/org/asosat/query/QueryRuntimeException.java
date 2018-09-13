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
package org.asosat.query;

import org.asosat.kernel.exception.KernelRuntimeException;

/**
 * asosat-script
 *
 * @author bingo 下午5:47:42
 *
 */
public class QueryRuntimeException extends KernelRuntimeException {

  private static final long serialVersionUID = 5993406707944953781L;

  /**
   *
   */
  public QueryRuntimeException() {
    super();
  }

  /**
   * @param message
   */
  public QueryRuntimeException(String message) {
    super(message);
  }

  /**
   * @param message
   * @param cause
   */
  public QueryRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public QueryRuntimeException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  /**
   * @param cause
   */
  public QueryRuntimeException(Throwable cause) {
    super(cause);
  }

}

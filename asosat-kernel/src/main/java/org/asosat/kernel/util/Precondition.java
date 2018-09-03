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
package org.asosat.kernel.util;

import java.util.function.Predicate;

public interface Precondition {

  @FunctionalInterface
  public static interface BiPrecondition<T> {
    T testAndReturn(T obj, T x, Object code, Object... variants);
  }

  @FunctionalInterface
  public static interface BoolPrecondition<T> {
    T testAndReturn(T obj, Predicate<T> p, Object code, Object... variants);
  }

  @FunctionalInterface
  public static interface ComparablePrecondition<T extends Comparable<T>> {
    T testAndReturn(T obj, T cmprObj, Object code, Object... msgPvariantsarams);
  }

  @FunctionalInterface
  public static interface SinglePrecondition<T> {
    T testAndReturn(T obj, Object code, Object... variants);
  }
}

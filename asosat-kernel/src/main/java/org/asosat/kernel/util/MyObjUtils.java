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

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author bingo 下午2:51:32
 *
 */
public class MyObjUtils {

  public final static Object[] EMPTY_ARGS = new Object[0];

  public MyObjUtils() {}

  public static <T> int compare(T a, T b, Comparator<? super T> c) {
    return Objects.compare(a, b, c);
  }

  public static <T, P> T computeIfNonNull(Supplier<P> sup, Function<P, T> func) {
    return sup == null || sup.get() == null ? null : func.apply(sup.get());
  }

  public static <T> T computeIfNull(Supplier<T> sup, Supplier<T> nulSup) {
    return sup == null || sup.get() == null ? nulSup.get() : sup.get();
  }

  public static boolean isDeepEquals(Object a, Object b) {
    return Objects.deepEquals(a, b);
  }

  public static boolean isEquals(Object a, Object b) {
    return Objects.equals(a, b);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static <T extends Number & Comparable> boolean isEquals(T a, T b) {
    return (a == b) || (a != null && b != null && a.compareTo(b) == 0);
  }

  public static int hash(Object... values) {
    return Objects.hash(values);
  }


  public static int hashCode(Object o) {
    return Objects.hashCode(o);
  }

  public static boolean isNull(Object obj) {
    return Objects.isNull(obj);
  }

  public static <T extends Comparable<T>> T max(T a, T b) {
    return a.compareTo(b) >= 0 ? a : b;
  }

  public static <T extends Comparable<T>> T min(T a, T b) {
    return a.compareTo(b) < 0 ? a : b;
  }


  public static boolean isNonNull(Object obj) {
    return Objects.nonNull(obj);
  }

  public static String toString(Object o) {
    return Objects.toString(o);
  }

  public static String toString(Object o, String nullDefault) {
    return Objects.toString(o, nullDefault);
  }
}

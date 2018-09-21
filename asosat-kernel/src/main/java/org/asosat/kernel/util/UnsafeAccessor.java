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

import java.lang.reflect.Field;
import sun.misc.Unsafe;

/**
 * asosat-kernel
 *
 * @author bingo 下午7:00:37
 *
 */
@SuppressWarnings("restriction")
public class UnsafeAccessor {
  private static Unsafe UNSAFE = null;

  static {
    try {
      Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      UnsafeAccessor.UNSAFE = (sun.misc.Unsafe) field.get(null);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  public static Unsafe get() {
    return UnsafeAccessor.UNSAFE;
  }
}

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
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * @author bingo 下午8:10:18
 *
 */
public class MyFieldUtils extends FieldUtils {


  public static void loopFields(Class<?> clazz, Consumer<Field> fc) {
    loopFields(clazz, fc, null);
  }

  public static void loopFields(Class<?> clazz, Consumer<Field> fc, Predicate<Field> ff) {
    Field[] fields = FieldUtils.getAllFields(clazz);
    for (Field field : fields) {
      if (ff != null && !ff.test(field)) {
        continue;
      }
      try {
        fc.accept(field);
      } catch (Exception ex) {
        throw new IllegalStateException(
            "Not allowed to access field '" + field.getName() + "': " + ex);
      }
    }
  }

  public static void loopLocalFields(Class<?> clazz, Consumer<Field> fc) {
    for (Field field : clazz.getDeclaredFields()) {
      try {
        fc.accept(field);
      } catch (Exception ex) {
        throw new IllegalStateException(
            "Not allowed to access field '" + field.getName() + "': " + ex);
      }
    }
  }

  public static void loopLocalMethods(Class<?> clazz, Consumer<Method> mc) {
    Method[] methods = clazz.getMethods();
    for (Method method : methods) {
      try {
        mc.accept(method);
      } catch (Exception ex) {
        throw new IllegalStateException(
            "Not allowed to access method '" + method.getName() + "': " + ex);
      }
    }
  }


}

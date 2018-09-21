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
package org.asosat.kernel.pattern.command;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * asosat-kernel
 *
 * @author bingo 下午11:59:58
 *
 */
public class CommandContext {

  @SuppressWarnings("rawtypes")
  static final ThreadLocal<Map<Class<?>, Map>> registration =
      ThreadLocal.withInitial(() -> new HashMap<>());

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static <T, K> T computeIfAbsent(Class<T> cls, K key, Function<K, T> function) {
    Map<Class<?>, Map> map = registration.get();
    return (T) map.computeIfAbsent(cls, (c) -> new HashMap<>()).computeIfAbsent(key, function);
  }

  @SuppressWarnings("rawtypes")
  static void clear() {
    Map<Class<?>, Map> map = registration.get();
    if (map != null) {
      map.forEach((k, v) -> v.clear());
      map.clear();
    }
  }

}

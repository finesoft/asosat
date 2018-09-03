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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.commons.collections4.MapUtils;
import org.asosat.kernel.supertype.DynamicAttributes.DynamicAttributeMap;

public abstract class MyMapUtils {

  public static DynamicAttributeMap asAttributeMap(Object... objects) {
    DynamicAttributeMap map = new DynamicAttributeMap();
    String key = null;
    int len = objects.length;
    for (int i = 0; i < len; i++) {
      if ((i & 1) == 0) {
        key = ConvertUtils.toString(objects[i]);
        map.put(key, null);
      } else {
        Object v = ConvertUtils.toObject(objects[i]);
        map.replace(key, v);
        key = null;
      }
    }
    return map;
  }

  /**
   * Collect an array to the map, array is filled like [K,V,K,V...].
   *
   * @param objects
   * @return bingo 下午3:32:58
   */
  public static <K, V> Map<K, V> asMap(Object... objects) {
    Map<K, V> map = new LinkedHashMap<>();
    K key = null;
    int len = objects.length;
    for (int i = 0; i < len; i++) {
      if ((i & 1) == 0) {
        key = ConvertUtils.toObject(objects[i]);
        map.put(key, null);
      } else {
        V v = ConvertUtils.toObject(objects[i]);
        map.replace(key, v);
        key = null;
      }
    }
    return map;
  }

  public static Properties asProperties(String... strings) {
    Map<String, String> map = asMap(Arrays.asList(strings).toArray());
    Properties p = new Properties();
    map.forEach((k, v) -> {
      p.put(k, v);
    });
    return p;
  }

  /**
   * Return enum instance from supplied map and key and enum class type, the value with supplied key
   * may be String/int.
   *
   * @param map
   * @param key
   * @param enumClazz
   * @return bingo 下午4:01:15
   */
  public static <T extends Enum<T>> T getMapEnumValue(final Map<?, ?> map, final Object key,
      final Class<T> enumClazz) {
    T value = map != null && key != null && map.containsKey(key)
        ? ConvertUtils.toEnum(map.get(key), enumClazz)
        : null;
    return value;
  }

  public static <T> T getMapValue(final Map<?, ?> map, final Object key,
      final BiFunction<Object, T, T> extractor, final T dfltVal) {
    T value =
        map != null && key != null && map.containsKey(key) ? extractor.apply(map.get(key), dfltVal)
            : null;
    return value == null ? dfltVal : value;
  }

  /**
   * Return expect value of type from supplied map with key and convertor. Usage: BigDecimal value =
   * getMapValue(map,"key",Convertor::toBigDecimal)
   *
   * @param map
   * @param key
   * @param extractor
   * @param dfltVal
   * @return bingo 下午4:03:08
   */
  public static <T> T getMapValue(final Map<?, ?> map, final Object key,
      final Function<Object, T> extractor) {
    return map != null && key != null && map.containsKey(key) ? extractor.apply(map.get(key))
        : null;
  }

  public static <T> List<T> getMapValueList(final Map<?, ?> map, final Object key,
      final Function<Object, List<T>> extractor) {
    return map != null && key != null && map.containsKey(key) ? extractor.apply(map.get(key))
        : null;
  }

  public static <T> T getMapValueOrElse(final Map<?, ?> map, final Object key,
      final Function<Object, T> extractor, final Supplier<T> sup) {
    T value =
        map != null && key != null && map.containsKey(key) ? extractor.apply(map.get(key)) : null;
    return value == null ? sup.get() : value;
  }

  public static <K, V> Map<V, K> invertMap(final Map<K, V> map) {
    return MapUtils.invertMap(map);
  }

  public static Map<String, Object> toMap(final ResourceBundle resourceBundle) {
    return MapUtils.toMap(resourceBundle);
  }

  public static <K, V> Properties toProperties(final Map<K, V> map) {
    return MapUtils.toProperties(map);
  }

}

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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.commons.collections4.MapUtils;
import org.asosat.kernel.abstraction.DynamicAttributes.DynamicAttributeMap;
import org.asosat.kernel.normal.conversion.Conversions;

public class MyMapUtils {

  public static DynamicAttributeMap asAttributeMap(Object... objects) {
    DynamicAttributeMap map = new DynamicAttributeMap();
    String key = null;
    int len = objects.length;
    for (int i = 0; i < len; i++) {
      if ((i & 1) == 0) {
        key = Conversions.toString(objects[i]);
        map.put(key, null);
      } else {
        Object v = Conversions.toObject(objects[i]);
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
        key = Conversions.toObject(objects[i]);
        map.put(key, null);
      } else {
        V v = Conversions.toObject(objects[i]);
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

  public static BigDecimal getMapBigDecimal(final Map<?, ?> map, final Object key) {
    return getMapObject(map, key, Conversions::toBigDecimal);
  }

  public static BigDecimal getMapBigDecimal(final Map<?, ?> map, final Object key,
      BigDecimal dflt) {
    return getMapObject(map, key, Conversions::toBigDecimal, dflt);
  }

  public static BigInteger getMapBigInteger(final Map<?, ?> map, final Object key) {
    return getMapObject(map, key, Conversions::toBigInteger);
  }

  public static BigInteger getMapBigInteger(final Map<?, ?> map, final Object key,
      BigInteger dflt) {
    return getMapObject(map, key, Conversions::toBigInteger, dflt);
  }

  public static Boolean getMapBoolean(final Map<?, ?> map, final Object key) {
    return getMapObject(map, key, Conversions::toBoolean);
  }

  public static Currency getMapCurrency(final Map<?, ?> map, final Object key) {
    return getMapObject(map, key, Conversions::toCurrency);
  }

  public static Currency getMapCurrency(final Map<?, ?> map, final Object key, Currency dflt) {
    return getMapObject(map, key, Conversions::toCurrency, dflt);
  }

  public static Double getMapDouble(final Map<?, ?> map, final Object key) {
    return getMapObject(map, key, Conversions::toDouble);
  }

  public static Double getMapDouble(final Map<?, ?> map, final Object key, Double dflt) {
    return getMapObject(map, key, Conversions::toDouble, dflt);
  }

  public static <T extends Enum<T>> T getMapEnum(final Map<?, ?> map, final Object key,
      final Class<T> enumClazz) {
    return map != null && key != null && map.containsKey(key)
        ? Conversions.toEnum(map.get(key), enumClazz)
        : null;
  }

  public static <T extends Enum<T>> T getMapEnum(final Map<?, ?> map, final Object key,
      final Class<T> enumClazz, T dflt) {
    T enumObj = getMapEnum(map, key, enumClazz);
    return enumObj == null ? dflt : enumObj;
  }

  public static Float getMapFloat(final Map<?, ?> map, final Object key) {
    return getMapObject(map, key, Conversions::toFloat);
  }

  public static Float getMapFloat(final Map<?, ?> map, final Object key, Float dflt) {
    return getMapObject(map, key, Conversions::toFloat, dflt);
  }

  public static Instant getMapInstant(final Map<?, ?> map, final Object key) {
    return getMapObject(map, key, Conversions::toInstant);
  }

  public static Instant getMapInstant(final Map<?, ?> map, final Object key, Instant dflt) {
    return getMapObject(map, key, Conversions::toInstant, dflt);
  }

  public static Integer getMapInteger(final Map<?, ?> map, final Object key) {
    return getMapObject(map, key, Conversions::toInteger);
  }

  public static Integer getMapInteger(final Map<?, ?> map, final Object key, Integer dflt) {
    return getMapObject(map, key, Conversions::toInteger, dflt);
  }

  @SuppressWarnings("unchecked")
  public static <T> List<T> getMapList(final Map<?, ?> map, final Object key) {
    return getMapList(map, key, o -> (T) o);
  }

  public static <T> List<T> getMapList(final Map<?, ?> map, final Object key,
      final Function<Object, T> objFunc) {
    return getMapObjectList(map, key, (v) -> Conversions.toList(v, objFunc));
  }

  public static LocalDate getMapLocalDate(final Map<?, ?> map, final Object key) {
    return getMapObject(map, key, Conversions::toLocalDate);
  }

  public static LocalDate getMapLocalDate(final Map<?, ?> map, final Object key, LocalDate dflt) {
    return getMapObject(map, key, Conversions::toLocalDate, dflt);
  }

  public static Locale getMapLocale(final Map<?, ?> map, final Object key) {
    return getMapObject(map, key, Conversions::toLocale);
  }

  public static Locale getMapLocale(final Map<?, ?> map, final Object key, Locale dflt) {
    return getMapObject(map, key, Conversions::toLocale, dflt);
  }

  public static Long getMapLong(final Map<?, ?> map, final Object key) {
    return getMapObject(map, key, Conversions::toLong);
  }

  public static Long getMapLong(final Map<?, ?> map, final Object key, Long dflt) {
    return getMapObject(map, key, Conversions::toLong, dflt);
  }

  @SuppressWarnings("unchecked")
  public static <K, V> Map<K, V> getMapMap(final Map<?, ?> map, final Object key) {
    Object v = map.get(key);
    if (v == null) {
      return null;
    } else if (v instanceof Map) {
      return (Map<K, V>) v;
    } else {
      throw new RuntimeException("Can't get map from key " + key);
    }
  }

  public static <T> T getMapObject(final Map<?, ?> map, final Object key,
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
  public static <T> T getMapObject(final Map<?, ?> map, final Object key,
      final Function<Object, T> extractor) {
    return map != null && key != null && map.containsKey(key) ? extractor.apply(map.get(key))
        : null;
  }

  public static <T> List<T> getMapObjectList(final Map<?, ?> map, final Object key,
      final Function<Object, List<T>> extractor) {
    return map != null && key != null && map.containsKey(key) ? extractor.apply(map.get(key))
        : null;
  }

  public static <T> T getMapObjectOrElse(final Map<?, ?> map, final Object key,
      final Function<Object, T> extractor, final Supplier<T> sup) {
    T value =
        map != null && key != null && map.containsKey(key) ? extractor.apply(map.get(key)) : null;
    return value == null ? sup.get() : value;
  }

  public static Short getMapShort(final Map<?, ?> map, final Object key) {
    return getMapObject(map, key, Conversions::toShort);
  }

  public static Short getMapShort(final Map<?, ?> map, final Object key, Short dflt) {
    return getMapObject(map, key, Conversions::toShort, dflt);
  }

  public static String getMapString(final Map<?, ?> map, final Object key) {
    return getMapObject(map, key, Conversions::toString);
  }

  public static String getMapString(final Map<?, ?> map, final Object key, String dflt) {
    String att = getMapObject(map, key, Conversions::toString);
    return att == null ? dflt : att;
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

  private MyMapUtils() {
    super();
  }

}

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

import static org.asosat.kernel.util.MyMapUtils.getMapEnumValue;
import static org.asosat.kernel.util.MyMapUtils.getMapValue;
import static org.asosat.kernel.util.MyMapUtils.getMapValueList;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * @author bingo 下午10:20:44
 *
 */
public interface WrappedMap<K, V> extends Map<K, V> {

  @Override
  default void clear() {
    unwrap().clear();
  }

  @Override
  default boolean containsKey(Object key) {
    return unwrap().containsKey(key);
  }

  @Override
  default boolean containsValue(Object value) {
    return unwrap().containsValue(value);
  }

  @Override
  default Set<Entry<K, V>> entrySet() {
    return unwrap().entrySet();
  }

  @Override
  default V get(Object key) {
    return unwrap().get(key);
  }

  default BigDecimal getBigDecimal(K key) {
    return getMapValue(unwrap(), key, ConvertUtils::toBigDecimal);
  }

  default BigDecimal getBigDecimal(K key, BigDecimal dflt) {
    return getMapValue(unwrap(), key, ConvertUtils::toBigDecimal, dflt);
  }

  default BigInteger getBigInteger(K key) {
    return getMapValue(unwrap(), key, ConvertUtils::toBigInteger);
  }

  default BigInteger getBigInteger(K key, BigInteger dflt) {
    return getMapValue(unwrap(), key, ConvertUtils::toBigInteger, dflt);
  }

  default Boolean getBoolean(K key) {
    return getMapValue(unwrap(), key, ConvertUtils::toBoolean);
  }

  default Currency getCurrency(K key) {
    return getMapValue(unwrap(), key, ConvertUtils::toCurrency);
  }

  default Currency getCurrency(K key, Currency dflt) {
    return getMapValue(unwrap(), key, ConvertUtils::toCurrency, dflt);
  }

  default Double getDouble(K key) {
    return getMapValue(unwrap(), key, ConvertUtils::toDouble);
  }

  default Double getDouble(K key, Double dflt) {
    return getMapValue(unwrap(), key, ConvertUtils::toDouble, dflt);
  }

  default <T extends Enum<T>> T getEnum(K key, final Class<T> enumClazz) {
    return getMapEnumValue(unwrap(), key, enumClazz);
  }

  default <T extends Enum<T>> T getEnum(K key, final Class<T> enumClazz, T dflt) {
    T enumObj = this.getEnum(key, enumClazz);
    return enumObj == null ? dflt : enumObj;
  }

  default Float getFloat(K key) {
    return getMapValue(unwrap(), key, ConvertUtils::toFloat);
  }

  default Float getFloat(K key, Float dflt) {
    return getMapValue(unwrap(), key, ConvertUtils::toFloat, dflt);
  }

  default Instant getInstant(K key) {
    return getMapValue(unwrap(), key, ConvertUtils::toInstant);
  }

  default Instant getInstant(K key, Instant dflt) {
    return getMapValue(unwrap(), key, ConvertUtils::toInstant, dflt);
  }

  default Integer getInteger(K key) {
    return getMapValue(unwrap(), key, ConvertUtils::toInteger);
  }

  default Integer getInteger(K key, Integer dflt) {
    return getMapValue(unwrap(), key, ConvertUtils::toInteger, dflt);
  }

  @SuppressWarnings("unchecked")
  default <T> List<T> getList(K key) {
    return getList(key, o -> (T) o);
  }

  default <T> List<T> getList(K key, final Function<Object, T> objFunc) {
    return getMapValueList(unwrap(), key, (v) -> ConvertUtils.toList(v, objFunc));
  }

  default LocalDate getLocalDate(K key) {
    return getMapValue(unwrap(), key, ConvertUtils::toLocalDate);
  }

  default LocalDate getLocalDate(K key, LocalDate dflt) {
    return getMapValue(unwrap(), key, ConvertUtils::toLocalDate, dflt);
  }

  default Locale getLocale(K key) {
    return getMapValue(unwrap(), key, ConvertUtils::toLocale);
  }

  default Locale getLocale(K key, Locale dflt) {
    return getMapValue(unwrap(), key, ConvertUtils::toLocale, dflt);
  }

  default Long getLong(K key) {
    return getMapValue(unwrap(), key, ConvertUtils::toLong);
  }

  default Long getLong(K key, Long dflt) {
    return getMapValue(unwrap(), key, ConvertUtils::toLong, dflt);
  }

  @SuppressWarnings("rawtypes")
  default Map getMap(K key) {
    V v = unwrap().get(key);
    if (v == null) {
      return null;
    } else if (v instanceof Map) {
      return (Map) v;
    } else {
      throw new RuntimeException("Can't get map from key " + key);
    }
  }

  default Short getShort(K key) {
    return getMapValue(unwrap(), key, ConvertUtils::toShort);
  }

  default Short getShort(K key, Short dflt) {
    return getMapValue(unwrap(), key, ConvertUtils::toShort, dflt);
  }

  default String getString(K key) {
    return getMapValue(unwrap(), key, ConvertUtils::toString);
  }

  default String getString(K key, String dflt) {
    String att = getMapValue(unwrap(), key, ConvertUtils::toString);
    return att == null ? dflt : att;
  }

  WrappedMap<K, V> getSubset(K key);

  @Override
  default boolean isEmpty() {
    return unwrap().isEmpty();
  }

  @Override
  default Set<K> keySet() {
    return unwrap().keySet();
  }

  @Override
  default V put(K key, V value) {
    return unwrap().put(key, value);
  }

  @Override
  default void putAll(Map<? extends K, ? extends V> m) {
    unwrap().putAll(m);
  }

  default void putAll(WrappedMap<? extends K, ? extends V> m) {
    unwrap().putAll(m);
  }

  @Override
  default V remove(Object key) {
    return unwrap().remove(key);
  }

  default boolean same(K key, WrappedMap<K, V> other) {
    return same(key, other, key);
  }

  default boolean same(K key, WrappedMap<K, V> other, K keyInOther) {
    return other != null && Objects.equals(unwrap().get(key), other.unwrap().get(keyInOther));
  }

  @Override
  default int size() {
    return unwrap().size();
  }

  default Map<K, V> unwrap() {
    return Collections.emptyMap();
  }

  @Override
  default Collection<V> values() {
    return unwrap().values();
  }

}

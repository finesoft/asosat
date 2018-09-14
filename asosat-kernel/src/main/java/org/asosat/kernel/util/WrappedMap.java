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

import static org.asosat.kernel.util.MyMapUtils.getMapBigDecimal;
import static org.asosat.kernel.util.MyMapUtils.getMapBigInteger;
import static org.asosat.kernel.util.MyMapUtils.getMapBoolean;
import static org.asosat.kernel.util.MyMapUtils.getMapCurrency;
import static org.asosat.kernel.util.MyMapUtils.getMapDouble;
import static org.asosat.kernel.util.MyMapUtils.getMapEnum;
import static org.asosat.kernel.util.MyMapUtils.getMapFloat;
import static org.asosat.kernel.util.MyMapUtils.getMapInstant;
import static org.asosat.kernel.util.MyMapUtils.getMapInteger;
import static org.asosat.kernel.util.MyMapUtils.getMapList;
import static org.asosat.kernel.util.MyMapUtils.getMapLocalDate;
import static org.asosat.kernel.util.MyMapUtils.getMapLocale;
import static org.asosat.kernel.util.MyMapUtils.getMapLong;
import static org.asosat.kernel.util.MyMapUtils.getMapValue;
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
    return getMapBigDecimal(unwrap(), key);
  }

  default BigDecimal getBigDecimal(K key, BigDecimal dflt) {
    return getMapBigDecimal(unwrap(), key, dflt);
  }

  default BigInteger getBigInteger(K key) {
    return getMapBigInteger(unwrap(), key);
  }

  default BigInteger getBigInteger(K key, BigInteger dflt) {
    return getMapBigInteger(unwrap(), key, dflt);
  }

  default Boolean getBoolean(K key) {
    return getMapBoolean(unwrap(), key);
  }

  default Currency getCurrency(K key) {
    return getMapCurrency(unwrap(), key);
  }

  default Currency getCurrency(K key, Currency dflt) {
    return getMapCurrency(unwrap(), key, dflt);
  }

  default Double getDouble(K key) {
    return getMapDouble(unwrap(), key);
  }

  default Double getDouble(K key, Double dflt) {
    return getMapDouble(unwrap(), key, dflt);
  }

  default <T extends Enum<T>> T getEnum(K key, final Class<T> enumClazz) {
    return getMapEnum(unwrap(), key, enumClazz);
  }

  default <T extends Enum<T>> T getEnum(K key, final Class<T> enumClazz, T dflt) {
    return getMapEnum(unwrap(), key, enumClazz, dflt);
  }

  default Float getFloat(K key) {
    return getMapFloat(unwrap(), key);
  }

  default Float getFloat(K key, Float dflt) {
    return getMapFloat(unwrap(), key, dflt);
  }

  default Instant getInstant(K key) {
    return getMapInstant(unwrap(), key);
  }

  default Instant getInstant(K key, Instant dflt) {
    return getMapInstant(unwrap(), key, dflt);
  }

  default Integer getInteger(K key) {
    return getMapInteger(unwrap(), key);
  }

  default Integer getInteger(K key, Integer dflt) {
    return getMapInteger(unwrap(), key, dflt);
  }

  default <T> List<T> getList(K key) {
    return getMapList(unwrap(), key);
  }

  default <T> List<T> getList(K key, final Function<Object, T> objFunc) {
    return getMapList(unwrap(), key, objFunc);
  }

  default LocalDate getLocalDate(K key) {
    return getMapLocalDate(unwrap(), key);
  }

  default LocalDate getLocalDate(K key, LocalDate dflt) {
    return getMapLocalDate(unwrap(), key, dflt);
  }

  default Locale getLocale(K key) {
    return getMapLocale(unwrap(), key);
  }

  default Locale getLocale(K key, Locale dflt) {
    return getMapLocale(unwrap(), key, dflt);
  }

  default Long getLong(K key) {
    return getMapLong(unwrap(), key);
  }

  default Long getLong(K key, Long dflt) {
    return getMapLong(unwrap(), key, dflt);
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

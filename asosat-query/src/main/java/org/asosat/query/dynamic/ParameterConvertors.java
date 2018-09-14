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
package org.asosat.query.dynamic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.asosat.kernel.util.ConvertUtils;

/**
 * asosat-query
 *
 * @author bingo 下午4:27:36
 *
 */
public class ParameterConvertors {

  final static Map<Class<?>, ParameterConvertor<?>> PROVIDER_MAP = new HashMap<>();
  static {
    PROVIDER_MAP.put(String.class, ConvertUtils::toString);
    PROVIDER_MAP.put(Long.class, ConvertUtils::toLong);
    PROVIDER_MAP.put(Integer.class, ConvertUtils::toInteger);
    PROVIDER_MAP.put(Short.class, ConvertUtils::toShort);
    PROVIDER_MAP.put(Boolean.class, ConvertUtils::toBoolean);
    PROVIDER_MAP.put(Double.class, ConvertUtils::toDouble);
    PROVIDER_MAP.put(Float.class, ConvertUtils::toFloat);
    PROVIDER_MAP.put(BigDecimal.class, ConvertUtils::toBigDecimal);
    PROVIDER_MAP.put(BigInteger.class, ConvertUtils::toBigInteger);
    PROVIDER_MAP.put(Character.class, ConvertUtils::toCharacter);
    PROVIDER_MAP.put(Currency.class, ConvertUtils::toCurrency);
    PROVIDER_MAP.put(Instant.class, ConvertUtils::toInstant);
    PROVIDER_MAP.put(LocalDate.class, ConvertUtils::toLocalDate);
    PROVIDER_MAP.put(Locale.class, ConvertUtils::toLocale);
    PROVIDER_MAP.put(ZonedDateTime.class, ConvertUtils::toZonedDateTime);
    PROVIDER_MAP.put(TimeZone.class, ConvertUtils::toTimeZone);
  }

  @SuppressWarnings("unchecked")
  public static <T> T convert(Object raw, Class<T> targetClass) {
    if (raw == null) {
      return null;
    } else if (PROVIDER_MAP.containsKey(targetClass)) {
      return (T) PROVIDER_MAP.get(targetClass).convert(raw);
    } else {
      return (T) raw;
    }
  }

}

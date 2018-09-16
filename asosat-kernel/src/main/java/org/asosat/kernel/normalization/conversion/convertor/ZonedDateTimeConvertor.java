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
package org.asosat.kernel.normalization.conversion.convertor;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import org.apache.commons.beanutils.converters.AbstractConverter;
import org.asosat.kernel.normalization.conversion.Convertor;

/**
 * asosat-kernel
 *
 * @author bingo 上午1:27:09
 *
 */
public class ZonedDateTimeConvertor extends AbstractConverter implements Convertor {



  /**
   *
   */
  public ZonedDateTimeConvertor() {
    super();
  }

  /**
   * @param defaultValue
   */
  public ZonedDateTimeConvertor(ZonedDateTime defaultValue) {
    super(defaultValue);
  }

  public static ZonedDateTime toZonedDateTime(Object obj, ZonedDateTime dfltVal) {
    if (obj instanceof ZonedDateTime) {
      return (ZonedDateTime) obj;
    } else if (obj instanceof java.sql.Date) {
      return ((java.sql.Date) obj).toLocalDate().atStartOfDay(ZoneId.systemDefault());
    } else if (obj instanceof Long || obj instanceof Temporal || obj instanceof java.util.Date) {
      return InstantConvertor.toInstant(obj, null).atZone(ZoneId.systemDefault());
    } else if (obj != null) {
      return ZonedDateTime.parse(obj.toString());
    } else {
      return dfltVal;
    }
  }

  @Override
  protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
    if (type != null && type.equals(ZonedDateTime.class)) {
      return type.cast(toZonedDateTime(value, null));
    }
    throw this.conversionException(type, value);
  }

  @Override
  protected Class<?> getDefaultType() {
    return ZonedDateTime.class;
  }

}

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
package org.asosat.kernel.normal.conversion.convertor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.List;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.AbstractConverter;
import org.asosat.kernel.normal.conversion.Convertor;
import org.asosat.kernel.util.MyBagUtils;

/**
 * asosat-kernel
 *
 * @author bingo 上午1:00:56
 *
 */
public class LocalDateConvertor extends AbstractConverter implements Convertor {

  public LocalDateConvertor() {
    super();
  }

  /**
   * @param defaultValue
   */
  public LocalDateConvertor(LocalDate defaultValue) {
    super(defaultValue);
  }

  static LocalDate toLocalDate(Object obj, LocalDate dfltVal) {
    if (obj instanceof LocalDate) {
      return (LocalDate) obj;
    } else if (obj instanceof List || obj instanceof Object[]) {
      if (MyBagUtils.getSize(obj) >= 3) {
        Integer y = Integer.class.cast(ConvertUtils.convert(MyBagUtils.get(obj, 0), Integer.class));
        Integer m = Integer.class.cast(ConvertUtils.convert(MyBagUtils.get(obj, 1), Integer.class));
        Integer d = Integer.class.cast(ConvertUtils.convert(MyBagUtils.get(obj, 2), Integer.class));
        return LocalDate.of(y, m, d);
      } else {
        throw new ConversionException(
            "Can't convert value '" + obj + "' to type " + LocalDate.class);
      }
    } else if (obj instanceof java.sql.Date) {
      return ((java.sql.Date) obj).toLocalDate();
    } else if (obj instanceof Long || obj instanceof java.util.Date || obj instanceof Temporal) {
      return InstantConvertor.toInstant(obj, null).atZone(ZoneId.systemDefault()).toLocalDate();
    } else if (obj != null) {
      return LocalDate.parse(obj.toString());
    } else {
      return dfltVal;
    }
  }

  @Override
  protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
    if (type != null && type.equals(LocalDate.class)) {
      return type.cast(toLocalDate(value, null));
    }
    throw this.conversionException(type, value);
  }

  @Override
  protected Class<?> getDefaultType() {
    return LocalDate.class;
  }


}

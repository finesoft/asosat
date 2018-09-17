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

import java.util.TimeZone;
import org.apache.commons.beanutils.converters.AbstractConverter;
import org.asosat.kernel.normal.conversion.Convertor;

/**
 * asosat-kernel
 *
 * @author bingo 上午1:25:21
 *
 */
public class TimeZoneConvertor extends AbstractConverter implements Convertor {

  public TimeZoneConvertor() {
    super();
  }

  /**
   * @param defaultValue
   */
  public TimeZoneConvertor(TimeZone defaultValue) {
    super(defaultValue);
  }

  public static TimeZone toTimeZone(Object obj) {
    if (obj == null) {
      return null;
    } else if (obj instanceof TimeZone) {
      return (TimeZone) obj;
    } else {
      return TimeZone.getTimeZone(obj.toString());
    }
  }

  @Override
  protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
    if (type != null && type.equals(TimeZone.class)) {
      return type.cast(toTimeZone(value));
    }
    throw this.conversionException(type, value);
  }

  @Override
  protected Class<?> getDefaultType() {
    return null;
  }

}

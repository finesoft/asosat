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

import java.time.Instant;
import java.time.temporal.Temporal;
import org.apache.commons.beanutils.converters.AbstractConverter;
import org.asosat.kernel.normal.conversion.Convertor;

/**
 * asosat-kernel
 *
 * @author bingo 上午12:56:12
 *
 */
public class InstantConvertor extends AbstractConverter implements Convertor {

  public InstantConvertor() {
    super();
  }

  /**
   * @param defaultValue
   */
  public InstantConvertor(Instant defaultValue) {
    super(defaultValue);
  }

  public static Instant toInstant(Object obj, Instant dfltVal) {
    if (obj instanceof Instant) {
      return (Instant) obj;
    } else if (obj instanceof Long) {
      return Instant.ofEpochMilli((Long) obj);
    } else if (obj instanceof Temporal) {
      return Instant.from((Temporal) obj);
    } else if (obj instanceof java.util.Date) {
      return Instant.ofEpochMilli(((java.util.Date) obj).getTime());
    } else if (obj != null) {
      return Instant.parse(obj.toString());
    } else {
      return dfltVal;
    }
  }

  @Override
  protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
    if (type != null && type.equals(Instant.class)) {
      return type.cast(toInstant(value, null));
    }
    throw this.conversionException(type, value);
  }

  @Override
  protected Class<?> getDefaultType() {
    return Instant.class;
  }

}

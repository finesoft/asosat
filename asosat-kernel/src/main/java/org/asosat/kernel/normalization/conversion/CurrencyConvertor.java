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
package org.asosat.kernel.normalization.conversion;

import java.util.Currency;
import org.apache.commons.beanutils.converters.AbstractConverter;

/**
 * asosat-kernel
 *
 * @author bingo 上午12:47:19
 *
 */
public class CurrencyConvertor extends AbstractConverter implements Convertor {


  @Override
  protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
    if (type != null && type.equals(Currency.class)) {
      if (value instanceof Currency) {
        return type.cast(value);
      } else {
        return type.cast(Currency.getInstance(value.toString()));
      }
    }
    throw this.conversionException(type, value);
  }

  @Override
  protected Class<?> getDefaultType() {
    return Currency.class;
  }

}

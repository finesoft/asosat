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

import org.apache.commons.beanutils.converters.AbstractConverter;

/**
 * asosat-kernel
 *
 * @author bingo 上午12:28:27
 *
 */
public class EnumConvertor extends AbstractConverter implements Convertor {


  public static void main(String... strings) {
    System.out.println(Enum.class.isEnum());
  }

  public static <T extends Enum<T>> T toEnum(Object obj, Class<T> enumClazz) {
    if (obj instanceof Enum<?> && obj.getClass().isAssignableFrom(enumClazz)) {
      return enumClazz.cast(obj);
    } else if (obj != null) {
      String str = obj.toString();
      if (str.chars().allMatch(Character::isDigit)) {
        return enumClazz.getEnumConstants()[Integer.parseInt(str)];
      } else {
        return Enum.valueOf(enumClazz, str);
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
    if (type != null && type.isEnum()) {
      @SuppressWarnings("rawtypes")
      Class _type = type;
      Object obj = toEnum(value, _type);
      return type.cast(obj);
    }
    throw this.conversionException(type, value);
  }

  @Override
  protected Class<?> getDefaultType() {
    return null;
  }

}

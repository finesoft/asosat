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

import org.apache.commons.beanutils.converters.BooleanConverter;
import org.asosat.kernel.normal.conversion.Convertor;

/**
 * asosat-kernel
 *
 * @author bingo 下午5:15:01
 *
 */
public class BooleanConvertor implements Convertor {

  BooleanConverter delegate =
      new BooleanConverter(new String[] {"true", "yes", "y", "on", "1", "是"},
          new String[] {"false", "no", "n", "off", "0", "否"}, false);

  @Override
  public <T> T convert(Class<T> type, Object value) {
    return this.delegate.convert(type, value);
  }

}

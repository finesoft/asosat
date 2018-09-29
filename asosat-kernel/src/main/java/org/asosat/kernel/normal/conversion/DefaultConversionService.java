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
package org.asosat.kernel.normal.conversion;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;

/**
 * asosat-kernel
 *
 * @author bingo 上午12:11:08
 *
 */
@ApplicationScoped
public class DefaultConversionService implements ConversionService {

  @Override
  public <T> T convert(Object value, Class<T> clazz) {
    return Convertors.instance.convertx(value, clazz);
  }

  @Override
  public void deregister(Class<?> clazz) {
    Convertors.instance.deregister(clazz);
  }

  @Override
  public Convertor getConvertor(Class<?> targetType) {
    return Conversions.getConvertor(targetType);
  }

  @Override
  public Convertor getConvertor(Class<?> sourceType, Class<?> targetType) {
    return Conversions.getConvertor(sourceType, targetType);
  }


  @SuppressWarnings("unchecked")
  @Override
  public <T> List<T> listConvert(Object value, Class<T> clazz) {
    return Convertors.instance.convertx(value, List.class, clazz);
  }



  @Override
  public <T> void register(Convertor convertor, Class<T> clazz) {
    Convertors.instance.register(convertor, clazz);
  }

}

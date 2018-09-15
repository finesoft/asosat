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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;

/**
 * asosat-kernel
 *
 * @author bingo 上午12:11:08
 *
 */
@ApplicationScoped
public class DefaultConvertors implements Convertors {

  ConvertUtilsBean provider = BeanUtilsBean.getInstance().getConvertUtils();

  EnumConvertor enumConvertor = new EnumConvertor();

  @Override
  public Object convert(Object value, Class<?> clazz) {
    this.autoRegisterEnumConvertor(clazz);
    return this.provider.convert(value, clazz);
  }

  @Override
  public void deregister(Class<?> clazz) {
    this.provider.deregister(clazz);
  }

  @Override
  public Convertor getConvertor(Class<?> targetType) {
    return (Convertor) this.provider.lookup(targetType);
  }

  @Override
  public Converter getConvertor(Class<?> sourceType, Class<?> targetType) {
    return this.provider.lookup(sourceType, targetType);
  }

  @Override
  public <T> void register(Convertor convertor, Class<T> clazz) {
    this.provider.register(convertor, clazz);
  }

  protected void autoRegisterEnumConvertor(Class<?> clazz) {
    if (clazz != null && clazz.isEnum() && this.provider.lookup(clazz) == null) {
      synchronized (this) {
        if (this.provider.lookup(clazz) == null) {
          this.provider.register(this.enumConvertor, clazz);
        }
      }
    }
  }

  @PostConstruct
  void init() {
    this.provider.register(new InstantConvertor(), Instant.class);
    this.provider.register(new LocalDateConvertor(), LocalDate.class);
    this.provider.register(new CurrencyConvertor(), Currency.class);
    this.provider.register(new TimeZoneConvertor(), TimeZone.class);
    this.provider.register(new ZonedDateTimeConvertor(), ZonedDateTime.class);
  }

}

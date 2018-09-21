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
package org.asosat.indices.elastic.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 组成id的片段，组成id的片段目前仅支基本类型及字符串，如果是对象类型则调用 {@link #Object.toString()}方法强制转为字符
 *
 * @author bingo 2018年2月5日
 * @since
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface EsIdSegmConfig {

  /**
   * 类型
   *
   * @return
   */
  Class<?> clazz() default String.class;

  /**
   * 段长度(字节数)，一般用于字符串型
   *
   * @return
   */
  short length() default 1;

  /**
   * 属性名称
   *
   * @return
   */
  String name();
}

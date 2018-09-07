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
/**
 * @author bingo 下午8:57:42
 *
 */
package org.asosat.elastic.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Boolean fields accept JSON true and false values, but can also accept strings which are
 * interpreted as either true or false
 *
 * @author bingo 2017年3月3日
 * @since
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface EsBoolean {

  /**
   * Mapping field-level query time boosting. Accepts a floating point number, defaults to 1.0.
   *
   * @return
   */
  float boost() default 1.0f;

  /**
   * Should the field be stored on disk in a column-stride fashion, so that it can later be used for
   * sorting, aggregations, or scripting? Accepts true (default) or false.
   *
   * @return
   */
  boolean doc_values() default true;

  /**
   * Should the field be searchable? Accepts true (default) and false.
   *
   * @return
   */
  boolean index() default true;

  /**
   * Accepts any of the true or false values string. The value is substituted for any explicit null
   * values. Defaults to false.
   *
   * @return
   */
  String null_value() default "null";

  /**
   * Whether the field value should be stored and retrievable separately from the _source field.
   * Accepts true or false (default).
   *
   * @return
   */
  boolean store() default false;
}

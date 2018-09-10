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
package org.asosat.search.elastic.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fielddata filtering can be used to reduce the number of terms loaded into memory, and thus reduce
 * memory usage. Terms can be filtered by frequency:
 *
 * The frequency filter allows you to only load terms whose document frequency falls between a min
 * and max value, which can be expressed an absolute number (when the number is bigger than 1.0) or
 * as a percentage (eg 0.01 is 1% and 1.0 is 100%). Frequency is calculated per segment. Percentages
 * are based on the number of docs which have a value for the field, as opposed to all docs in the
 * segment.
 *
 * Small segments can be excluded completely by specifying the minimum number of docs that the
 * segment should contain with min_segment_size
 *
 * @author bingo 2017年3月3日
 * @since
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface EsFielddataFrequencyFilter {

  float max();

  float min();

  int min_segment_size();
}

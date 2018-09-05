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
package org.asosat.kernel.domain.saga;

import java.time.temporal.Temporal;
import org.asosat.kernel.abstraction.DynamicAttributes.DynamicAttributeType;
import org.asosat.kernel.abstraction.DynamicAttributes.DynamicNamedAttribute;

/**
 * @author bingo 下午5:49:42
 *
 */
public interface SagaAttribute extends DynamicNamedAttribute {

  Boolean getBoolValue();

  Number getNumberValue();

  String getStringValue();

  Temporal getTemporalValue();

  @SuppressWarnings("unchecked")
  @Override
  default <T> T getValue() {
    if (getType() == DynamicAttributeType.BOOL) {
      return getBoolValue() == null ? null : (T) getBoolValue();
    } else if (getType() == DynamicAttributeType.NUMBER) {
      return getNumberValue() == null ? null : (T) getNumberValue();
    } else if (getType() == DynamicAttributeType.TEMPORAL) {
      return getTemporalValue() == null ? null : (T) getTemporalValue();
    } else {
      return getStringValue() == null ? null : (T) getStringValue();
    }
  }
}

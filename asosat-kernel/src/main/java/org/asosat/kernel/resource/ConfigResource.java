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
package org.asosat.kernel.resource;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.enterprise.context.ApplicationScoped;

/**
 * @author bingo 上午10:18:15
 *
 */
@ApplicationScoped
public interface ConfigResource {

  /**
   * All config properties
   */
  Map<String, String> getProperties();


  default Set<String> getPropertyNames() {
    return getProperties().keySet();
  }

  <T> T getValue(String propertyName, final Function<Object, T> extractor);

  default <T> T getValue(String propertyName, final Function<Object, T> extractor, T dflt) {
    T value = getValue(propertyName, extractor);
    return value == null ? dflt : value;
  }

}

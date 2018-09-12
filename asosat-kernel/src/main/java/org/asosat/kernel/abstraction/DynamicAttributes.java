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
package org.asosat.kernel.abstraction;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.asosat.kernel.exception.KernelRuntimeException;
import org.asosat.kernel.util.WrappedMap;

/**
 * @author bingo 下午5:46:15
 *
 */
public interface DynamicAttributes {

  public static class DynamicAttributeMap implements WrappedMap<String, Object> {

    private final Map<String, Object> map = new LinkedHashMap<>();

    public DynamicAttributeMap() {}

    public DynamicAttributeMap(DynamicAttributeMap other) {
      if (other != null) {
        putAll(other);
      }
    }

    public DynamicAttributeMap(Map<String, Object> map) {
      if (map != null) {
        putAll(map);
      }
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      DynamicAttributeMap other = (DynamicAttributeMap) obj;
      return map.equals(other.map);
    }

    @Override
    public DynamicAttributeMap getSubset(String key) {
      Object obj = unwrap().get(key);
      if (obj == null) {
        return null;
      } else if (obj instanceof DynamicAttributeMap) {
        return (DynamicAttributeMap) obj;
      } else {
        throw new KernelRuntimeException("Can't get subset from key " + key);
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + map.hashCode();
      return result;
    }

    public DynamicAttributeMap unmodifiable() {
      return new DynamicAttributeMap() {
        final Map<String, Object> unmodifiable = Collections.unmodifiableMap(map);

        @Override
        public Map<String, Object> unwrap() {
          return unmodifiable;
        }
      };
    }

    @Override
    public Map<String, Object> unwrap() {
      return this.map;
    }

  }

  public enum DynamicAttributeType {
    ENUM, STRING, BOOL, NUMBER, TEMPORAL
  }

  public static interface DynamicNamedAttribute {

    String getName();

    DynamicAttributeType getType();

    <T> T getValue();

  }
}

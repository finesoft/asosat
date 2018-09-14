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

import static org.asosat.kernel.util.Preconditions.requireNotNull;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author bingo 下午4:23:02
 * @since
 */
public interface Aggregate extends Entity, Being, Readable<Aggregate> {

  /**
   * If flush is true then the integration event queue will be clear
   */
  List<Message> extractMessages(boolean flush);

  /**
   * Fire events.
   */
  void fire(Event event, Annotation... qualifiers);

  /**
   * In this case, it means whether it is persisted or not
   */
  default boolean isEnabled() {
    return getLifecycle() == Lifecycle.ENABLED;
  }

  /**
   * The aggregate isn't persisted, or is destroyed, but still live in memory until the GC recycle.
   */
  Boolean isPhantom();

  /**
   * Raise messages
   */
  void raise(Message... messages);


  public static interface AggregateIdentifier extends EntityIdentifier {

    @Override
    public Serializable getId();

    @Override
    public String getType();

  }

  public static class DefaultAggregateIdentifier implements AggregateIdentifier {

    private static final long serialVersionUID = 416267416396865273L;

    private Serializable id;

    private String type;

    public DefaultAggregateIdentifier() {}

    public DefaultAggregateIdentifier(Aggregate aggregate) {
      this.id = requireNotNull(requireNotNull(aggregate, "").getId(), "");
      this.type = requireNotNull(aggregate.getClass().getName(), "");// FIXME MSG
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      DefaultAggregateIdentifier other = (DefaultAggregateIdentifier) obj;
      if (this.id == null) {
        if (other.id != null) {
          return false;
        }
      } else if (!this.id.equals(other.id)) {
        return false;
      }
      if (this.type == null) {
        if (other.type != null) {
          return false;
        }
      } else if (!this.type.equals(other.type)) {
        return false;
      }
      return true;
    }

    @Override
    public Serializable getId() {
      return this.id;
    }

    @Override
    public String getType() {
      return this.type;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
      result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
      return result;
    }


  }

}

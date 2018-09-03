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
package org.asosat.domain.aggregate;

import java.util.logging.Logger;
import javax.persistence.MappedSuperclass;
import org.asosat.kernel.supertype.Entity;

/**
 * @author bingo 2018年3月28日
 * @since
 */
@MappedSuperclass
public abstract class AbstractEntity implements Entity {

  private static final long serialVersionUID = -4508714167429852231L;

  protected final transient Logger logger = Logger.getLogger(this.getClass().toString());

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || !(o instanceof AbstractEntity)) {
      return false;
    }
    if (this.getId() == null || ((AbstractEntity) o).getId() == null) {
      return super.equals(o);
    }
    return this.getId().equals(((AbstractEntity) o).getId());
  }

  @Override
  public int hashCode() {
    return this.getId() == null ? super.hashCode() : this.getId().hashCode();
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " [id=" + this.getId() + "]";
  }

}
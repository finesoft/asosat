/*
 * Copyright (c) 2013-2018, Bingo.Chen (finesoft@gmail.com).
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
package org.corant.asosat.ddd.domain.shared;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Map;

import static org.corant.shared.util.MapUtils.getMapLong;
import static org.corant.shared.util.MapUtils.getMapString;

@Deprecated
@MappedSuperclass
public abstract class AbstractPPTReference extends AbstractReference implements Nameable, Numbered {

  private static final long serialVersionUID = -8160589662074054451L;

  @Column(name = "referenceName")
  private String name;

  @Column(name = "referenceNumber")
  private String number;

  public AbstractPPTReference(Long id, Long vn, String name, String number) {
    setId(id);
    setVn(vn);
    setName(name);
    setNumber(number);
  }

  public AbstractPPTReference(Object obj) {
    if (obj instanceof Map) {
      Map<?, ?> mapObj = Map.class.cast(obj);
      setId(getMapLong(mapObj, "id"));
      setVn(getMapLong(mapObj, "vn"));
      setName(getMapString(mapObj, "name"));
      setNumber(getMapString(mapObj, "number"));
    } else if (obj instanceof AbstractPPTReference) {
      AbstractPPTReference other = AbstractPPTReference.class.cast(obj);
      setId(other.getId());
      setVn(other.getVn());
      setName(other.getName());
      setNumber(other.getNumber());
    }
  }

  protected AbstractPPTReference() {}

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    AbstractPPTReference other = (AbstractPPTReference) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (number == null) {
      if (other.number != null) {
        return false;
      }
    } else if (!number.equals(other.number)) {
      return false;
    }
    return true;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getNumber() {
    return number;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (name == null ? 0 : name.hashCode());
    result = prime * result + (number == null ? 0 : number.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "AbstractPPTReference [name=" + name + ", number=" + number + ", getId()=" + getId()
        + ", getVn()=" + getVn() + "]";
  }

  @Override
  protected void setId(Long id) {
    super.setId(id);
  }

  protected void setName(String name) {
    this.name = name;
  }

  protected void setNumber(String number) {
    this.number = number;
  }

  @Override
  protected void setVn(Long vn) {
    super.setVn(vn);
  }

}
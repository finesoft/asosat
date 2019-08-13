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

import static org.corant.shared.util.MapUtils.getMapLong;

import java.util.Map;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
public abstract class AbstractLiteValue implements ValueObject {

    private static final long serialVersionUID = -8160589662074054451L;

    @Column
    private Long id;

    protected AbstractLiteValue(Long id) {
        setId(id);
    }

    public AbstractLiteValue(Object obj) {
        if (obj instanceof Map) {
            Map<?, ?> mapObj = Map.class.cast(obj);
            setId(getMapLong(mapObj, "id"));
        } else if (obj instanceof AbstractLiteValue) {
            AbstractLiteValue other = AbstractLiteValue.class.cast(obj);
            setId(other.getId());
        }
    }

    protected AbstractLiteValue() {
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
        AbstractLiteValue other = (AbstractLiteValue) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    public Long getId() {
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id == null ? 0 : id.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Industry [id=" + getId() + "]";
    }

    protected void setId(Long id) {
        this.id = id;
    }
}

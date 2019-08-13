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
import static org.corant.shared.util.MapUtils.getMapString;

import java.util.Map;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Deprecated
@MappedSuperclass
public abstract class AbstractNumberedLiteValue extends AbstractLiteValue implements Numbered {

    private static final long serialVersionUID = -8160589662074054451L;

    @Column(name = "referenceNumber")
    private String number;

    public AbstractNumberedLiteValue(Long id, String number) {
        super(id);
        setNumber(number);
    }

    public AbstractNumberedLiteValue(Object obj) {
        if (obj instanceof Map) {
            Map<?, ?> mapObj = Map.class.cast(obj);
            setId(getMapLong(mapObj, "id"));
            setNumber(getMapString(mapObj, "number"));
        } else if (obj instanceof AbstractNumberedLiteValue) {
            AbstractNumberedLiteValue other = AbstractNumberedLiteValue.class.cast(obj);
            setId(other.getId());
            setNumber(other.getNumber());
        }
    }

    protected AbstractNumberedLiteValue() {
    }

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
        AbstractNumberedLiteValue other = (AbstractNumberedLiteValue) obj;
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
    public String getNumber() {
        return number;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (number == null ? 0 : number.hashCode());
        return result;
    }

    @Override
    protected void setId(Long id) {
        super.setId(id);
    }

    protected void setNumber(String number) {
        this.number = number;
    }
}

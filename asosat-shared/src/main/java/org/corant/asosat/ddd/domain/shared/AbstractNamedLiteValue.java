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
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Deprecated
@MappedSuperclass
public abstract class AbstractNamedLiteValue extends AbstractLiteValue implements Nameable {

    private static final long serialVersionUID = -8160589662074054451L;

    @Column(name = "referenceVn")
    private Long vn;

    @Column(name = "referenceName")
    private String name;

    protected AbstractNamedLiteValue(Long id, Long vn, String name) {
        super(id);
        setVn(vn);
        setName(name);
    }

    protected AbstractNamedLiteValue(Map<?,?> mapObj) {
        super(getMapLong(mapObj, "id"));
        setVn(getMapLong(mapObj, "vn"));
        setName(getMapString(mapObj, "name"));
    }

    protected AbstractNamedLiteValue() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AbstractNamedLiteValue that = (AbstractNamedLiteValue) o;
        return Objects.equals(vn, that.vn) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), vn, name);
    }

    public Long getVn() {
        return vn;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected void setId(Long id) {
        super.setId(id);
    }

    protected void setVn(Long vn) {
        this.vn = vn;
    }

    protected void setName(String name) {
        this.name = name;
    }
}

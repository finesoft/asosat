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
package org.asosat.shared;

import static org.corant.shared.util.Conversions.toLong;
import static org.corant.shared.util.Maps.getMapLong;
import static org.corant.shared.util.Maps.getMapString;
import java.security.Principal;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;

/**
 * @author bingo 上午10:11:36
 */
@Embeddable
@MappedSuperclass
public class Participator implements Principal, ValueObject {

  private static final long serialVersionUID = -7820136962102596705L;

  public static final String CURRENT_USER_KEY = "_currentUser";
  public static final String CURRENT_ORG_KEY = "_currentOrg";
  public static final String CURRENT_USER_ID_KEY = "_currentUserId";
  public static final String CURRENT_ORG_ID_KEY = "_currentOrgId";

  @Column(name = "participatorId")
  private Long id;

  @Column(name = "participatorName")
  private String name;

  public Participator(Long id, String name) {
    super();
    setId(id);
    setName(name);
  }

  public <K, V> Participator(Map<K, V> mapObj) {
    this(getMapLong(mapObj, "id"), getMapString(mapObj, "name"));
  }

  protected Participator() {}

  @SuppressWarnings("unchecked")
  public static Participator of(Object obj) {
    if (obj instanceof Map) {
      return new Participator(Map.class.cast(obj));
    } else if (obj instanceof Party) {
      Party party = Party.class.cast(obj);
      return new Participator(toLong(party.getId()), party.getName());
    } else if (obj instanceof Participator) {
      return Participator.class.cast(obj);
    }
    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Participator that = (Participator) o;
    return Objects.equals(id, that.id) && Objects.equals(name, that.name);
  }

  public Long getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }

  protected void setId(Long id) {
    this.id = id;
  }

  protected void setName(String name) {
    this.name = name;
  }
}

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
package org.asosat.ddd.domain.model;

import static org.corant.suites.bundle.Preconditions.requireNotNull;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.corant.suites.bundle.GlobalMessageCodes;
import org.corant.suites.ddd.model.AbstractEntity;
import org.hibernate.annotations.GenericGenerator;

/**
 * corant-asosat-ddd
 *
 * @author bingo 下午1:22:17
 *
 */
@MappedSuperclass
public abstract class AbstractDefaultEntity extends AbstractEntity {

  private static final long serialVersionUID = -4395445831789674052L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "snowflake")
  @GenericGenerator(name = "snowflake",
      strategy = "org.asosat.ddd.util.GlobalUUIDGenerator$GlobalHibernateUUIDGenerator")
  private Long id;

  public AbstractDefaultEntity() {}

  @Override
  public Long getId() {
    return id;
  }

  protected void setId(Long id) {
    this.id = requireNotNull(id, GlobalMessageCodes.ERR_PARAM);
  }
}

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
package org.asosat.thorntail.domain.aggregate;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.asosat.domain.aggregate.AbstractEntity;
import org.hibernate.annotations.GenericGenerator;

/**
 * asosat-thorntail
 *
 * @author bingo 下午3:04:07
 *
 */
public class AbstractGeneralEntity extends AbstractEntity {

  private static final long serialVersionUID = -1285350802273071827L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "snowflake")
  @GenericGenerator(name = "snowflake",
      strategy = "org.asosat.thorntail.provider.HibernateSnowflakeIdGenerator")
  private Long id;

  public AbstractGeneralEntity() {}

  @Override
  public Long getId() {
    return this.id;
  }


}

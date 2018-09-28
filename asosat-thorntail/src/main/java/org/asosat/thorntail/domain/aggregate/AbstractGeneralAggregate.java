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

import static org.asosat.kernel.util.Preconditions.requireNotNull;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.asosat.domain.aggregate.AbstractDefaultGenericAggregate;
import org.asosat.kernel.resource.GlobalMessageCodes;
import org.hibernate.annotations.GenericGenerator;

/**
 * @author bingo 下午7:39:58
 *
 */
@MappedSuperclass
public abstract class AbstractGeneralAggregate<P, T extends AbstractGeneralAggregate<P, T>>
    extends AbstractDefaultGenericAggregate<P, T> {

  private static final long serialVersionUID = 3926909112607573627L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @GenericGenerator(name = "snowflake",
      strategy = "org.asosat.thorntail.provider.HibernateSnowflakeIdGenerator")
  private Long id;

  public AbstractGeneralAggregate() {}

  @Override
  public Long getId() {
    return this.id;
  }

  protected void setId(Long id) {
    this.id = requireNotNull(id, GlobalMessageCodes.ERR_PARAM);
  }

}

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
package org.asosat.thorntail.example.providers;

import java.sql.Types;
import org.hibernate.dialect.SQLServer2012Dialect;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.type.StandardBasicTypes;

/**
 * @author bingo 下午5:42:16
 *
 */
public class HibernateSQLServer2012Dialect extends SQLServer2012Dialect {

  private static final int MAX_LENGTH = 8000;

  public HibernateSQLServer2012Dialect() {
    this.registerColumnType(Types.VARCHAR, "nvarchar($l)");
    this.registerColumnType(Types.VARCHAR, MAX_LENGTH, "nvarchar($l)");
    this.registerColumnType(Types.CLOB, "nvarchar(MAX)");
    this.registerHibernateType(Types.NVARCHAR, StandardBasicTypes.STRING.getName());
    this.registerHibernateType(Types.NCLOB, StandardBasicTypes.STRING.getName());
    this.registerHibernateType(Types.LONGNVARCHAR, StandardBasicTypes.STRING.getName());
  }

  @Override
  public NameQualifierSupport getNameQualifierSupport() {
    return NameQualifierSupport.NONE;
  }

}

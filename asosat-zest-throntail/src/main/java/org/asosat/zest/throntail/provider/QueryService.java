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
package org.asosat.zest.throntail.provider;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.asosat.kernel.exception.KernelRuntimeException;
import org.asosat.query.sql.AbstractSqlNamedQuery;
import org.asosat.query.sql.DefaultSqlQueryExecutor;
import org.asosat.query.sql.SqlQueryConfiguration;
import org.asosat.query.sql.paging.dialect.Dialect;
import org.asosat.query.sql.paging.dialect.SQLServer2012Dialect;

/**
 * asosat-zest-throntail
 *
 * @author bingo 下午1:22:51
 *
 */
@ApplicationScoped
public class QueryService extends AbstractSqlNamedQuery {
  SqlQueryConfiguration configuration;

  @Override
  protected SqlQueryConfiguration getConfiguration() {
    return this.configuration;
  }

  @PostConstruct
  protected void init() {
    try {
      InitialContext ctx = new InitialContext();
      this.configuration = new DmmsSqlQueryConfiguration(
          (DataSource) ctx.lookup("java:jboss/datasources/exampleRoDs"),
          new SQLServer2012Dialect());
      this.executor = new DefaultSqlQueryExecutor(this.configuration);
    } catch (NamingException e) {
      throw new KernelRuntimeException(e);
    }
  }

  static class DmmsSqlQueryConfiguration implements SqlQueryConfiguration {

    final DataSource ds;
    final Dialect da;

    DmmsSqlQueryConfiguration(DataSource ds, Dialect da) {
      this.ds = ds;
      this.da = da;
    }

    @Override
    public DataSource getDataSource() {
      return this.ds;
    }

    @Override
    public Dialect getDialect() {
      return this.da;
    }
  }
}
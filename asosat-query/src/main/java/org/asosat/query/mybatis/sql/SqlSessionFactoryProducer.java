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
package org.asosat.query.mybatis.sql;

import static org.asosat.kernel.util.MyMapUtils.asProperties;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import org.apache.ibatis.datasource.jndi.JndiDataSourceFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

/**
 * asosat-mybatis
 *
 * @author bingo 上午11:44:34
 *
 */
@ApplicationScoped
public abstract class SqlSessionFactoryProducer {

  @ApplicationScoped
  @Produces
  public SqlSessionFactory produce() {
    JndiDataSourceFactory jdsf = new JndiDataSourceFactory();
    jdsf.setProperties(asProperties(JndiDataSourceFactory.DATA_SOURCE, this.getDataSourceName()));
    Environment env = new Environment(this.getEnvironmentId(), new JdbcTransactionFactory(),
        jdsf.getDataSource());
    Configuration config = new Configuration(env);
    this.handleConfiguration(config);
    return new SqlSessionFactoryBuilder().build(config);
  }

  protected abstract String getDataSourceName();

  protected String getEnvironmentId() {
    return "asosatRoDs";
  }

  protected void handleConfiguration(Configuration config) {}
}

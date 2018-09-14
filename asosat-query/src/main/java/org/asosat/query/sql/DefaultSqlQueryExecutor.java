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
package org.asosat.query.sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.StatementConfiguration;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

/**
 * asosat-script
 *
 * @author bingo 下午5:31:55
 *
 */
public class DefaultSqlQueryExecutor implements SqlQueryExecutor {

  protected QueryRunner runner;

  public DefaultSqlQueryExecutor(SqlQueryConfiguration confiuration) {
    this.runner = new QueryRunner(confiuration.getDataSource(),
        new StatementConfiguration(confiuration.getFetchDirection(), confiuration.getFetchSize(),
            confiuration.getMaxFieldSize(), confiuration.getMaxRows(),
            confiuration.getQueryTimeout()));
  }

  @Override
  public Map<String, Object> get(String sql) throws SQLException {
    return this.getRunner().query(sql, new MapHandler());
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(String sql, Class<T> resultClass, Object... args) throws SQLException {
    if (Map.class.isAssignableFrom(resultClass)) {
      Object obj = this.getRunner().query(sql, new MapHandler(), args);
      return obj == null ? null : (T) obj;
    } else {
      return this.getRunner().query(sql, new BeanHandler<>(resultClass), args);
    }
  }

  @Override
  public List<Map<String, Object>> select(String sql) throws SQLException {
    List<Map<String, Object>> tmp = this.getRunner().query(sql, new MapListHandler());
    return tmp == null ? new ArrayList<>() : tmp;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> List<T> select(String sql, Class<T> resultClass, Object... args) throws SQLException {
    if (Map.class.isAssignableFrom(resultClass)) {
      @SuppressWarnings("rawtypes")
      List tmp = this.getRunner().query(sql, new MapListHandler(), args);
      return tmp == null ? new ArrayList<>() : tmp;
    } else {
      List<T> tmp = this.getRunner().query(sql, new BeanListHandler<>(resultClass), args);
      return tmp == null ? new ArrayList<>() : tmp;
    }
  }

  protected QueryRunner getRunner() {
    return this.runner;
  }
}

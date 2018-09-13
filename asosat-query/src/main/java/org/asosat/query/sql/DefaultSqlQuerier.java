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
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;

/**
 * asosat-script
 *
 * @author bingo 下午5:31:55
 *
 */
@ApplicationScoped
public class DefaultSqlQuerier implements SqlQuerier {

  @Inject
  DataSource dataSource;

  @Override
  public Map<String, Object> get(String sql) throws SQLException {
    return null;
  }

  @Override
  public <T> T get(String sql, Object[] args, int[] argTypes, RowMapper<T> mapper)
      throws SQLException {
    return null;
  }

  @Override
  public <T> T get(String sql, RowMapper<T> mapper, Object... args) throws SQLException {
    return null;
  }

  @Override
  public List<Map<String, Object>> select(String sql) throws SQLException {
    return null;
  }

  @Override
  public <T> List<T> select(String sql, Object[] args, int[] argTypes, RowMapper<T> mapper)
      throws SQLException {
    return null;
  }

  @Override
  public <T> List<T> select(String sql, RowMapper<T> mapper, Object... args) throws SQLException {
    return null;
  }

}

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
package org.asosat.query.sql.paging.dialect;

import org.asosat.query.sql.RemoveHelper;

/**
 * asosat-query
 *
 * @author bingo 上午10:58:26
 *
 */
public interface Dialect {

  public static final String SELECT = "SELECT ";
  public static final int SELECT_LEN = SELECT.length() - 1;
  public static final String SELECT_DISTINCT = "SELECT DISTINCT ";
  public static final int SELECT_DISTINCT_LEN = SELECT_DISTINCT.length() - 1;
  public static final String ORDER_BY = "ORDER BY ";
  public static final int ORDER_BY_LEN = ORDER_BY.length() - 1;

  public static String getNonOrderByPart(String sql) {
    return RemoveHelper.removeOrders(sql);
  }


  /**
   * Convert SQL statement to Count SQL statement
   *
   * @param sql to convert SQL
   * @return Count SQL statement
   */
  default String getCountSql(String sql) {
    return new StringBuilder(sql.length() + 40).append("SELECT COUNT(1) FROM ( ")
        .append(Dialect.getNonOrderByPart(sql)).append(" ) AS tmp_count_").toString();
  }

  /**
   * Convert SQL statement to Paging SQL
   *
   * @param sql to convert SQL
   * @param offset begin offset
   * @param limit page size
   * @return Paging SQL statement
   */
  String getLimitSql(String sql, int offset, int limit);

  /**
   *
   * @return supportsLimit
   */
  boolean supportsLimit();

  public static enum DBMS {
    MYSQL, ORACLE, DB2, H2, HSQL, POSTGRE, SQLSERVER, SQLSERVER2005, SYBASE,
  }
}

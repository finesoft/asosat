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

/**
 * asosat-query
 *
 * @author bingo 上午11:46:53
 *
 */
public class SQLServer2000Dialect extends SQLServerDialect {

  static int getAfterSelectInsertPoint(String sql) {
    int selectIndex = sql.toLowerCase().indexOf(SELECT);
    final int selectDistinctIndex = sql.toLowerCase().indexOf(SELECT_DISTINCT);
    return selectIndex + (selectDistinctIndex == selectIndex ? SELECT_DISTINCT_LEN : SELECT_LEN);
  }

  @Override
  public String getLimitSql(String sql, int offset, int limit) {
    return this.getLimitString(sql, offset, limit);
  }

  @Override
  public boolean supportsLimit() {
    return true;
  }

  /**
   * <pre>
   * dialect.getLimitString("select * from user", 12, ":offset",0,":limit") will return
   * select * from user limit :offset,:limit
   * </pre>
   */
  private String getLimitString(String sql, int offset, int limit) {
    if (offset > 0) {
      throw new UnsupportedOperationException(
          "The database SQLServer 2000 limit query not supported");
    }
    String upperCase = sql.toUpperCase();
    int insertPoint = upperCase.indexOf(SELECT)
        + (upperCase.contains(SELECT_DISTINCT) ? SELECT_DISTINCT_LEN : SELECT_LEN);
    return new StringBuilder(10 + sql.length()).append(sql)
        .insert(insertPoint, String.format(" TOP %d ", limit)).toString();
  }
}

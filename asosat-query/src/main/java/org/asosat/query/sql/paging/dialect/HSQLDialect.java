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

import org.asosat.query.sql.SqlHelper;

/**
 * asosat-query
 *
 * @author bingo 上午11:51:02
 *
 */
public class HSQLDialect implements Dialect {

  @Override
  public String getLimitSql(String sql, int offset, int limit) {
    return this.getLimitString(sql, offset, Integer.toString(offset), Integer.toString(limit));
  }

  /**
   * <pre>
   * dialect.getLimitString("select * from user", 12, ":offset",0,":limit") will return
   * select limit 0 12 * from user
   * </pre>
   */
  public String getLimitString(String sql, int offset, String offsetPlaceholder,
      String limitPlaceholder) {
    boolean hasOffset = offset > 0;
    return new StringBuffer(sql.length() + 10).append(sql)
        .insert(SqlHelper.shallowIndexOfPattern(sql, SqlHelper.SELECT_PATTERN, 0) + 7,
            hasOffset ? " LIMIT " + offsetPlaceholder + " " + limitPlaceholder
                : " TOP " + limitPlaceholder)
        .toString();
  }

  @Override
  public boolean supportsLimit() {
    return true;
  }

}

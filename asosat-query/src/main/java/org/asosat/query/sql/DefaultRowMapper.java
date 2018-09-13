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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.asosat.query.sql.SqlQuerier.ResultSetMetaDataExtractor;
import org.asosat.query.sql.SqlQuerier.RowMapper;

/**
 * asosat-script
 *
 * @author bingo 下午5:36:07
 *
 */
public class DefaultRowMapper implements RowMapper<Map<String, Object>> {

  @Override
  public Map<String, Object> mapRow(ResultSet rs, int rowNum,
      ResultSetMetaDataExtractor metaDataExtractor) throws SQLException {
    int columnCount = metaDataExtractor.getColumnCount();
    Map<String, Object> mapOfColValues = new LinkedHashMap<>(columnCount);
    for (int i = 1; i <= columnCount; i++) {
      String key = metaDataExtractor.getColumnName(i);
      key = key == null ? "_noname__" + i : key;
      Object obj = this.getColumnValue(rs, i);
      mapOfColValues.put(key, obj);
    }
    return mapOfColValues;
  }

  protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
    return JdbcUtils.getResultSetValue(rs, index);
  }
}

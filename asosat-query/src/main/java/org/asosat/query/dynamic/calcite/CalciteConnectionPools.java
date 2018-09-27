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
package org.asosat.query.dynamic.calcite;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.apache.calcite.jdbc.Driver;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 * asosat-query
 *
 * @author bingo 下午8:49:44
 *
 */
public class CalciteConnectionPools {

  static final Map<String, BasicDataSource> CPS = new ConcurrentHashMap<>();

  public static DataSource getDataSource(String name, Supplier<Properties> supplier) {
    return CPS.computeIfAbsent(name, (k) -> {
      BasicDataSource bds = new BasicDataSource();
      bds.setDefaultReadOnly(true);
      bds.setRollbackOnReturn(false);
      bds.setUrl(Driver.CONNECT_STRING_PREFIX);
      bds.setDriver(new Driver());
      bds.setInitialSize(4);
      Properties pops = supplier.get();
      if (pops != null) {
        pops.forEach((pk, pv) -> bds.addConnectionProperty(pk.toString(),
            pv == null ? null : pv.toString()));
      }
      return bds;
    });
  }
}

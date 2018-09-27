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
package org.asosat.query.calcite;

import static org.asosat.kernel.util.MyMapUtils.asProperties;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.asosat.query.QueryRuntimeException;
import org.asosat.query.dynamic.calcite.CalciteConnectionPools;
import org.junit.Test;
import com.google.common.io.Resources;

/**
 * asosat-query
 *
 * @author bingo 下午2:48:30
 *
 */
public class MongoTest {

  protected static final URL MODEL_URL = MongoTest.class.getResource("/mongo-model.json");

  public static String getInlineModelJson() {
    try {
      return "inline:" + Resources.toString(MODEL_URL, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new QueryRuntimeException(e);
    }
  }

  @Test
  public void test() throws SQLException {
    for (int t = 0; t < 4; t++) {
      new Thread(() -> {
        Long t1 = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
          List<Map<String, Object>> list;
          try {
            list = new QueryRunner(CalciteConnectionPools.getDataSource("mongo", () -> asProperties(
                CalciteConnectionProperty.MODEL.camelName(), getInlineModelJson()))).query(
                    "select cast(_MAP['genArtName'] AS varchar(255)) AS gan, cast(_MAP['artNumber'] AS varchar(255)) AS artNum, cast(_MAP['_id'] AS varchar(255)) AS id from \"mongo\".\"articlePublishPool\"",
                    new MapListHandler());
          } catch (SQLException e) {
            throw new QueryRuntimeException(e);
          }
          Map<String, Object> map = list.get(0);
          System.out.println(map.get("id") + "\t" + map.get("gan") + "\t" + map.get("artNum"));
        }
        System.out.println(System.currentTimeMillis() - t1);

      }).start();
    }
  }

}

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

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import org.apache.calcite.avatica.ConnectionProperty;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.runtime.FlatLists;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.junit.BeforeClass;
import org.junit.Test;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;

/**
 * asosat-query
 *
 * @author bingo 下午2:48:30
 *
 */
public class MongoTest {

  protected static final URL MODEL_URL = MongoTest.class.getResource("/mongo-model.json");

  protected static QueryRunner runner;

  protected static CalciteConnection connection;


  @BeforeClass
  public static void setUp() throws Exception {
    connection = new MapConnectionFactory(ImmutableMap.of(), ImmutableList.of())
        .with(CalciteConnectionProperty.MODEL,
            "inline:" + Resources.toString(MODEL_URL, StandardCharsets.UTF_8))
        .createConnection().unwrap(CalciteConnection.class);
    connection.getRootSchema().getTableNames().forEach(System.out::println);

    runner = new QueryRunner();
  }


  @Test
  public void test() throws SQLException {
    try {
      Long t1 = System.currentTimeMillis();
      for (int i = 0; i < 1000; i++) {
        List<Map<String, Object>> list = runner.query(connection,
            "select cast(_MAP['genArtName'] AS varchar(255)) AS gan, cast(_MAP['artNumber'] AS varchar(255)) AS artNum, cast(_MAP['_id'] AS varchar(255)) AS id from \"mongo\".\"articlePublishPool\"",
            new MapListHandler());
        Map<String, Object> map = list.get(0);
        System.out.println(map.get("id") + "\t" + map.get("gan") + "\t" + map.get("artNum"));
      }
      System.out.println(System.currentTimeMillis() - t1);
    } finally {
      connection.close();
    }
  }

  @FunctionalInterface
  public interface ConnectionPostProcessor {
    Connection apply(Connection connection) throws SQLException;
  }

  abstract static class ConnectionFactory {
    public abstract Connection createConnection() throws SQLException;

    public ConnectionFactory with(ConnectionPostProcessor postProcessor) {
      throw new UnsupportedOperationException();
    }

    public ConnectionFactory with(ConnectionProperty property, Object value) {
      throw new UnsupportedOperationException();
    }

    public ConnectionFactory with(String property, Object value) {
      throw new UnsupportedOperationException();
    }
  }
  static class MapConnectionFactory extends ConnectionFactory {
    private final ImmutableMap<String, String> map;
    private final ImmutableList<ConnectionPostProcessor> postProcessors;

    private MapConnectionFactory(ImmutableMap<String, String> map,
        ImmutableList<ConnectionPostProcessor> postProcessors) {
      this.map = Objects.requireNonNull(map);
      this.postProcessors = Objects.requireNonNull(postProcessors);
    }

    @Override
    public Connection createConnection() throws SQLException {
      final Properties info = new Properties();
      for (Map.Entry<String, String> entry : this.map.entrySet()) {
        info.setProperty(entry.getKey(), entry.getValue());
      }
      Connection connection = DriverManager.getConnection("jdbc:calcite:", info);
      for (ConnectionPostProcessor postProcessor : this.postProcessors) {
        connection = postProcessor.apply(connection);
      }
      return connection;
    }

    @Override
    public boolean equals(Object obj) {
      return this == obj || obj.getClass() == MapConnectionFactory.class
          && ((MapConnectionFactory) obj).map.equals(this.map)
          && ((MapConnectionFactory) obj).postProcessors.equals(this.postProcessors);
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.map, this.postProcessors);
    }

    @Override
    public ConnectionFactory with(ConnectionPostProcessor postProcessor) {
      ImmutableList.Builder<ConnectionPostProcessor> builder = ImmutableList.builder();
      builder.addAll(this.postProcessors);
      builder.add(postProcessor);
      return new MapConnectionFactory(this.map, builder.build());
    }

    @Override
    public ConnectionFactory with(ConnectionProperty property, Object value) {
      if (!property.type().valid(value, property.valueClass())) {
        throw new IllegalArgumentException();
      }
      return this.with(property.camelName(), value.toString());
    }

    @Override
    public ConnectionFactory with(String property, Object value) {
      return new MapConnectionFactory(FlatLists.append(this.map, property, value.toString()),
          this.postProcessors);
    }
  }

}

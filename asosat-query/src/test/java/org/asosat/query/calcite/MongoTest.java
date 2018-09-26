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

import java.sql.Connection;
import java.sql.DriverManager;
import org.apache.calcite.adapter.mongodb.MongoSchema;
import org.apache.calcite.adapter.mongodb.MongoSchemaFactory;
import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.SchemaPlus;
import org.junit.BeforeClass;

/**
 * asosat-query
 *
 * @author bingo 下午2:48:30
 *
 */
public class MongoTest {


  static MongoSchemaFactory schemaFactory = new MongoSchemaFactory();

  static MongoSchema schema;

  @BeforeClass
  public static void setUp() throws Exception {
    Class.forName("org.apache.calcite.jdbc.Driver");
    Connection connection = DriverManager.getConnection("jdbc:calcite:");
    CalciteConnection calciteConnection = connection.unwrap(CalciteConnection.class);
    SchemaPlus rootSchema = calciteConnection.getRootSchema();
  }


}

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
package org.asosat.thorntail.provider;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import io.thorntail.condition.annotation.RequiredClassPresent;
import io.thorntail.jdbc.DriverMetaData;

/**
 * @author bingo 上午11:04:52
 *
 */
@ApplicationScoped
@RequiredClassPresent("com.microsoft.sqlserver.jdbc.SQLServerDriver")
public class SQLServerDriverProducer {

  @Produces
  @ApplicationScoped
  DriverMetaData driverInfo() {
    return new DriverMetaData("com.microsoft.sqlserver.jdbc.SQLServerDriver")
        .setDriverClassName(com.microsoft.sqlserver.jdbc.SQLServerDriver.class.getName())
        .setDataSourceClassName(com.microsoft.sqlserver.jdbc.SQLServerDataSource.class.getName());
  }

}

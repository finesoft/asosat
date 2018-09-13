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
package org.asosat.query.mapping;

import static org.asosat.kernel.util.MyStrUtils.defaultString;
import static org.asosat.kernel.util.MyStrUtils.isNotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * asosat-query
 *
 * @author bingo 上午10:22:33
 *
 */
public class Query implements Serializable {

  private static final long serialVersionUID = -2142303696673387541L;

  String name;
  Class<?> resultClass = java.util.Map.class;
  Class<?> resultSetMapping;
  boolean cache = true;
  boolean cacheResultSetMetadata = true;
  String description;
  String script;
  List<FetchQuery> fetchQueries = new ArrayList<>();
  List<QueryHint> hints = new ArrayList<>();
  String version = "";

  /**
   * @return the description
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * @return the fetchQueries
   */
  public List<FetchQuery> getFetchQueries() {
    return this.fetchQueries;
  }

  /**
   * @return the hints
   */
  public List<QueryHint> getHints() {
    return this.hints;
  }

  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * @return the resultClass
   */
  public Class<?> getResultClass() {
    return this.resultClass;
  }

  /**
   * @return the resultSetMapping
   */
  public Class<?> getResultSetMapping() {
    return this.resultSetMapping;
  }

  /**
   * @return the script
   */
  public String getScript() {
    return this.script;
  }

  /**
   * @return the version
   */
  public String getVersion() {
    return this.version;
  }

  public String getVersionedName() {
    return defaultString(this.name) + (isNotBlank(this.version) ? "_" + this.version : "");
  }

  /**
   * @return the cache
   */
  public boolean isCache() {
    return this.cache;
  }

  /**
   * @return the cacheResultSetMetadata
   */
  public boolean isCacheResultSetMetadata() {
    return this.cacheResultSetMetadata;
  }
}

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * asosat-query
 *
 * @author bingo 上午10:22:33
 *
 */
public class Query implements Serializable {

  private static final long serialVersionUID = -2142303696673387541L;

  public static final String QUE_MAP_ELE = "query-mapping";
  public static final String QUE_ELE = "query";
  public static final String QUE_DESC_ELE = "description";
  public static final String QUE_SCPT_ELE = "script";
  public static final String QUE_FQ_ELE = "fetch-queries";
  public static final String QUE_HIT_ELE = "hint";

  public static final String ATT_NAME = "name";
  public static final String ATT_RST_CLS = "result-class";
  public static final String ATT_RST_SET_CLS = "result-set-mapping";
  public static final String ATT_CACHE = "cache";
  public static final String ATT_VER = "version";


  String name;
  Class<?> resultClass;
  Class<?> resultSetMapping;
  boolean cache;
  String description;
  String query;
  List<FetchQuery> fetchQueries = new ArrayList<>();
  Map<String, String> hints = new HashMap<>();
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
  public Map<String, String> getHints() {
    return this.hints;
  }

  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * @return the query
   */
  public String getQuery() {
    return this.query;
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

}

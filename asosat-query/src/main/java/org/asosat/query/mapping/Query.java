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
import java.util.stream.Collectors;

/**
 * asosat-query
 *
 * @author bingo 上午10:22:33
 *
 */
public class Query implements Serializable {

  private static final long serialVersionUID = -2142303696673387541L;

  private String name;
  private Class<?> resultClass = java.util.Map.class;
  private Class<?> resultSetMapping;
  private boolean cache = true;
  private boolean cacheResultSetMetadata = true;
  private String description;
  private String script;
  private List<FetchQuery> fetchQueries = new ArrayList<>();
  private List<QueryHint> hints = new ArrayList<>();
  private String version = "";
  private Map<String, ParameterMapping> paramMappings = new HashMap<>();

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
   * @return the paramMappings
   */
  public Map<String, ParameterMapping> getParamMappings() {
    return this.paramMappings;
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

  public List<String> getVersionedFetchQueryNames() {
    return this.getFetchQueries().stream().map(f -> f.getVersionedReferenceQueryName())
        .collect(Collectors.toList());
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

  void setCache(boolean cache) {
    this.cache = cache;
  }

  void setCacheResultSetMetadata(boolean cacheResultSetMetadata) {
    this.cacheResultSetMetadata = cacheResultSetMetadata;
  }

  void setDescription(String description) {
    this.description = description;
  }

  void setName(String name) {
    this.name = name;
  }

  void setResultClass(Class<?> resultClass) {
    this.resultClass = resultClass;
  }

  void setResultSetMapping(Class<?> resultSetMapping) {
    this.resultSetMapping = resultSetMapping;
  }

  void setScript(String script) {
    this.script = script;
  }

  void setVersion(String version) {
    this.version = version;
  }
}

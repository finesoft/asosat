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

import java.util.List;
import java.util.Map;
import org.asosat.query.ParameterResolver.Parameter;
import org.asosat.query.mapping.FetchQuery;
import org.asosat.query.sql.DefaultSqlQueryTemplate.ScriptAndParam;

/**
 * asosat-query
 *
 * @author bingo 下午4:35:55
 *
 */
public class DefaultSqlQueryParameter implements Parameter<String, Object[], FetchQuery> {

  final String script;
  final Object[] convertedParams;
  final Class<?> resultClass;
  final List<FetchQuery> fetchQueries;

  public DefaultSqlQueryParameter(DefaultSqlQueryTemplate tpl, Map<String, Object> param) {
    super();
    this.fetchQueries = tpl.fetchQueries;
    ScriptAndParam snp = tpl.process(param);
    this.script = snp.script;
    this.convertedParams = snp.params;
    this.resultClass = tpl.resultClass;
  }

  @Override
  public Object[] getConvertedParameters() {
    return this.convertedParams;
  }

  @Override
  public List<FetchQuery> getFetchQueries() {
    return this.fetchQueries;
  }

  /**
   * @return the resultClass
   */
  @SuppressWarnings("unchecked")
  @Override
  public <T> Class<T> getResultClass() {
    return (Class<T>) this.resultClass;
  }

  @Override
  public String getScript() {
    return this.script;
  }

}

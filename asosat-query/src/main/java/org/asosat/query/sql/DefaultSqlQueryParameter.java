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

import java.io.IOException;
import java.io.StringWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.asosat.query.ParameterResolver.Parameter;
import org.asosat.query.QueryRuntimeException;
import org.asosat.query.mapping.FetchQuery;
import org.asosat.query.mapping.ParameterMapping;
import org.asosat.query.mapping.Query;
import freemarker.template.Template;
import freemarker.template.TemplateException;

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

  public DefaultSqlQueryParameter(QueryTemplate tpl, Map<String, Object> param) {
    super();
    this.fetchQueries = tpl.fetchQueries;
    this.script = tpl.process(param);
    this.convertedParams = param.values().toArray(new Object[0]);
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

  public static class QueryTemplate {
    final String name;
    final Template scriptTpl;
    final Map<String, ParameterMapping> paramMappings;
    final long lastModified;
    final Class<?> resultClass;
    final List<FetchQuery> fetchQueries = new ArrayList<>();

    QueryTemplate(Query query) {
      this.fetchQueries.addAll(query.getFetchQueries());
      this.name = query.getName();
      this.resultClass = query.getResultClass() == null ? Map.class : query.getResultClass();
      try {
        this.scriptTpl =
            new Template(this.name, query.getScript(), DefaultSqlQueryParameterResolver.FM_CFG);
        this.paramMappings = Collections.unmodifiableMap(query.getParamMappings());
        this.lastModified = Instant.now().toEpochMilli();
      } catch (IOException e) {
        throw new QueryRuntimeException(e);
      }
    }

    String process(Map<String, Object> param) {
      StringWriter sw = new StringWriter();
      try {
        this.scriptTpl.process(param, sw);
      } catch (TemplateException | IOException | NullPointerException e) {
        throw new QueryRuntimeException("Freemarker process stringTemplate is error", e);
      }
      return sw.toString();
    }
  }
}

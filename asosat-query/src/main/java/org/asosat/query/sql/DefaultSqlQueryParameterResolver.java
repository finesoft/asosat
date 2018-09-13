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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.asosat.query.ParameterResolver;
import org.asosat.query.QueryRuntimeException;
import org.asosat.query.mapping.FetchQuery;
import org.asosat.query.mapping.Query;
import org.asosat.query.mapping.QueryMappingService;
import freemarker.template.Configuration;

/**
 * asosat-query
 *
 * @author bingo 下午3:16:56
 *
 */
@Dependent
public class DefaultSqlQueryParameterResolver
    implements ParameterResolver<String, Map<String, Object>, String, Object[], FetchQuery> {

  static final Configuration FM_CFG = new Configuration(Configuration.VERSION_2_3_28);

  final Map<String, DefaultSqlQueryTemplate> cachedQueryTpls = new ConcurrentHashMap<>();

  @Inject
  QueryMappingService mappingService;

  @Override
  public DefaultSqlQueryParameter resolve(String key, Map<String, Object> param) {
    return new DefaultSqlQueryParameter(
        this.cachedQueryTpls.computeIfAbsent(key, this::buildQueryTemplate), param);
  }

  protected DefaultSqlQueryTemplate buildQueryTemplate(String key) {
    Query query = this.mappingService.getQuery(key);
    if (query == null) {
      throw new QueryRuntimeException("Can not found Query for key " + key);
    }
    return new DefaultSqlQueryTemplate(query);
  }

}

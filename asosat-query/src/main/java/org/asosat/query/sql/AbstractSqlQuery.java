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

import static org.asosat.kernel.util.MyBagUtils.isEmpty;
import static org.asosat.kernel.util.MyMapUtils.getMapInteger;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.commons.beanutils.BeanUtils;
import org.asosat.kernel.abstraction.Query;
import org.asosat.query.ParameterResolver;
import org.asosat.query.ParameterResolver.Parameter;
import org.asosat.query.QueryRuntimeException;
import org.asosat.query.mapping.FetchQuery;
import org.asosat.query.mapping.FetchQuery.FetchQueryParameterSource;
import org.asosat.query.sql.paging.dialect.Dialect;

/**
 * asosat-script
 *
 * @author bingo 下午5:33:21
 *
 */
@ApplicationScoped
public abstract class AbstractSqlQuery implements Query<String, Map<String, Object>> {

  protected SqlQueryExecutor executor;

  @Inject
  ParameterResolver<String, Map<String, Object>, String, Object[], FetchQuery> paramResolver;

  @Override
  public <T> ContinuousList<T> continuing(String q, Map<String, Object> param) {
    int start = getMapInteger(param, "start", 1), limit = getMapInteger(param, "limit", 16);
    Parameter<String, Object[], FetchQuery> paramToUse = this.paramResolver.resolve(q, param);
    Class<T> rcls = paramToUse.getResultClass();
    Object[] queryParam = paramToUse.getConvertedParameters();
    List<FetchQuery> fetchQueries = paramToUse.getFetchQueries();
    String sql = paramToUse.getScript();
    String limitSql = this.getDialect().getLimitSql(sql, start, limit + 1);
    try {
      ContinuousList<T> result = ContinuousList.inst();
      List<T> list = this.executor.select(limitSql, rcls, queryParam);
      this.fetch(list, fetchQueries, param);
      if (list.size() > limit) {
        list.remove(list.size() - 1);
        result.withData(list);
        result.withHasNext(true);
      } else {
        result.withData(list);
      }
      return result;
    } catch (SQLException e) {
      throw new QueryRuntimeException(e);
    }
  }

  @Override
  public <T> T get(String q, Map<String, Object> param) {
    Parameter<String, Object[], FetchQuery> paramToUse = this.paramResolver.resolve(q, param);
    Class<T> rcls = paramToUse.getResultClass();
    Object[] queryParam = paramToUse.getConvertedParameters();
    List<FetchQuery> fetchQueries = paramToUse.getFetchQueries();
    String sql = paramToUse.getScript();
    try {
      T result = this.executor.get(sql, rcls, queryParam);
      this.fetch(result, fetchQueries, param);
      return result;
    } catch (SQLException e) {
      throw new QueryRuntimeException(e);
    }
  }

  @Override
  public <T> PagedList<T> paging(String q, Map<String, Object> param) {
    int start = getMapInteger(param, "start", 1), limit = getMapInteger(param, "limit", 16);
    Parameter<String, Object[], FetchQuery> paramToUse = this.paramResolver.resolve(q, param);
    Class<T> rcls = paramToUse.getResultClass();
    Object[] queryParam = paramToUse.getConvertedParameters();
    List<FetchQuery> fetchQueries = paramToUse.getFetchQueries();
    String sql = paramToUse.getScript();
    String limitSql = this.getDialect().getLimitSql(sql, start, limit);
    try {
      List<T> list = this.executor.select(limitSql, rcls, queryParam);
      PagedList<T> result = PagedList.inst();
      int count = list.size();
      if (count > (limit - start + 1)) {
        result.withTotal(start + count);
      } else {
        result.withTotal(getMapInteger(
            this.executor.get(this.getDialect().getCountSql(sql), Map.class, queryParam),
            Dialect.COUNT_FIELD_NAME));
      }
      this.fetch(list, fetchQueries, param);
      result.withData(list);
      return result;
    } catch (SQLException e) {
      throw new QueryRuntimeException(e);
    }
  }

  @Override
  public <T> List<T> select(String q, Map<String, Object> param) {
    Parameter<String, Object[], FetchQuery> paramToUse = this.paramResolver.resolve(q, param);
    Class<T> rcls = paramToUse.getResultClass();
    Object[] queryParam = paramToUse.getConvertedParameters();
    List<FetchQuery> fetchQueries = paramToUse.getFetchQueries();
    String sql = paramToUse.getScript();
    try {
      List<T> list = this.executor.select(sql, rcls, queryParam);
      this.fetch(list, fetchQueries, param);
      return list;
    } catch (SQLException e) {
      throw new QueryRuntimeException(e);
    }
  }

  @Override
  public <T> Stream<T> stream(String q, Map<String, Object> param) {
    return null;
  }

  protected <T> void fetch(List<T> list, List<FetchQuery> fetchQueries, Map<String, Object> param) {
    if (isEmpty(list) || isEmpty(fetchQueries)) {
      return;
    }
    list.forEach(e -> {
      fetchQueries.stream().forEach(f -> this.fetch(e, f, new HashMap<>(param)));
    });
  }

  @SuppressWarnings("unchecked")
  protected <T> void fetch(T obj, FetchQuery fetchQuery, Map<String, Object> param) {
    if (null == obj || fetchQuery == null) {
      return;
    }
    // handle fetch
    Map<String, Object> fetchParam = this.resolveFetchParam(obj, fetchQuery, param);
    int maxSize = fetchQuery.getMaxSize();
    String injectProName = fetchQuery.getInjectPropertyName(),
        refQueryName = fetchQuery.getVersionedReferenceQueryName();
    Parameter<String, Object[], FetchQuery> paramToUse =
        this.paramResolver.resolve(refQueryName, fetchParam);
    Class<?> rcls = fetchQuery.getResultClass() == null ? paramToUse.getResultClass()
        : fetchQuery.getResultClass();
    String sql = paramToUse.getScript();
    Object[] params = paramToUse.getConvertedParameters();
    List<FetchQuery> fetchQueries = paramToUse.getFetchQueries();
    if (maxSize > 0) {
      sql = this.getDialect().getLimitSql(sql, 1, maxSize);
    }
    try {
      List<?> list = this.executor.select(sql, rcls, params);
      if (obj instanceof Map) {
        Map.class.cast(obj).put(injectProName, list);
      } else {
        BeanUtils.setProperty(obj, injectProName, list);
      }
      this.fetch(list, fetchQueries, param);
    } catch (SQLException | IllegalAccessException | InvocationTargetException e) {
      throw new QueryRuntimeException(e);
    }
  }

  protected <T> void fetch(T obj, List<FetchQuery> fetchQueries, Map<String, Object> param) {
    if (null == obj || isEmpty(fetchQueries)) {
      return;
    }
    fetchQueries.stream().forEach(f -> this.fetch(obj, f, new HashMap<>(param)));
  }

  protected abstract SqlQueryConfiguration getConfiguration();

  protected Dialect getDialect() {
    return this.getConfiguration().getDialect();
  }

  protected Map<String, Object> resolveFetchParam(Object obj, FetchQuery fetchQuery,
      Map<String, Object> param) {
    Map<String, Object> pmToUse = new HashMap<>();
    fetchQuery.getParameters().forEach(p -> {
      if (p.getSource() == FetchQueryParameterSource.P) {
        pmToUse.put(p.getName(), param.get(p.getSourceName()));
      } else if (obj != null) {
        if (obj instanceof Map) {
          pmToUse.put(p.getName(), Map.class.cast(obj).get(p.getSourceName()));
        } else {
          try {
            pmToUse.put(p.getName(), BeanUtils.getProperty(obj, p.getSourceName()));
          } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new QueryRuntimeException(
                String.format("Can not extract value from query result for fetch query [%s] param!",
                    fetchQuery.getReferenceQuery()),
                e);
          }
        }
      }
    });
    return pmToUse;
  }
}

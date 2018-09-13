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

import static org.asosat.kernel.util.MyMapUtils.getMapValue;
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
import org.asosat.kernel.util.ConvertUtils;
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
  public <T> T get(String q, Map<String, Object> param) {
    Parameter<String, Object[], FetchQuery> paramToUse = this.paramResolver.resolve(q, param);
    Class<T> rcls = paramToUse.getResultClass();
    try {
      T obj = this.executor.get(paramToUse.getScript(), rcls, paramToUse.getConvertedParameters());
      this.fetch(obj, paramToUse.getFetchQueries(), param);
      return obj;
    } catch (SQLException e) {
      throw new QueryRuntimeException(e);
    }
  }

  @Override
  public <T> IteratedList<T> iterating(String q, Map<String, Object> param) {
    int start = getMapValue(param, "start", ConvertUtils::toInteger, 1),
        limit = getMapValue(param, "limit", ConvertUtils::toInteger, 16);
    Parameter<String, Object[], FetchQuery> paramToUse = this.paramResolver.resolve(q, param);
    Class<T> rcls = paramToUse.getResultClass();
    String sql = paramToUse.getScript();
    String limitSql = this.getDialect().getLimitSql(sql, start, limit + 1);
    try {
      IteratedList<T> il = IteratedList.inst();
      List<T> list = this.executor.select(limitSql, rcls, paramToUse.getConvertedParameters());
      this.fetch(list, paramToUse.getFetchQueries(), param);
      if (list.size() > limit) {
        list.remove(list.size() - 1);
        il.withData(list);
        il.withHasNext(true);
      } else {
        il.withData(list);
      }
      return il;
    } catch (SQLException e) {
      throw new QueryRuntimeException(e);
    }
  }

  @Override
  public <T> PagedList<T> paging(String q, Map<String, Object> param) {
    int start = getMapValue(param, "start", ConvertUtils::toInteger, 1),
        limit = getMapValue(param, "limit", ConvertUtils::toInteger, 16);
    Parameter<String, Object[], FetchQuery> paramToUse = this.paramResolver.resolve(q, param);
    Class<T> rcls = paramToUse.getResultClass();
    String sql = paramToUse.getScript();
    String limitSql = this.getDialect().getLimitSql(sql, start, limit);
    try {
      List<T> list = this.executor.select(limitSql, rcls, paramToUse.getConvertedParameters());
      PagedList<T> pl = PagedList.inst();
      int count = list.size();
      if (count > (limit - start)) {
        pl.withTotal(start + count);
      } else {
        pl.withTotal(getMapValue(this.executor.get(this.getDialect().getCountSql(sql)), "total",
            ConvertUtils::toInteger));
      }
      this.fetch(list, paramToUse.getFetchQueries(), param);
      pl.withData(list);
      return pl;
    } catch (SQLException e) {
      throw new QueryRuntimeException(e);
    }
  }

  @Override
  public <T> List<T> select(String q, Map<String, Object> param) {
    Parameter<String, Object[], FetchQuery> paramToUse = this.paramResolver.resolve(q, param);
    Class<T> rcls = paramToUse.getResultClass();
    try {
      List<T> list =
          this.executor.select(paramToUse.getScript(), rcls, paramToUse.getConvertedParameters());
      this.fetch(list, paramToUse.getFetchQueries(), param);
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
    list.forEach(e -> {
      fetchQueries.stream().forEach(f -> this.fetch(e, f, new HashMap<>(param)));
    });
  }

  protected <T> void fetch(T obj, FetchQuery fetchQuery, Map<String, Object> param) {
    // handle fetch
    Map<String, Object> fetchParam = this.resolveFetchParam(obj, fetchQuery, param);
    int maxSize = fetchQuery.getMaxSize();
    String injectProName = fetchQuery.getInjectPropertyName(),
        refQueryName = fetchQuery.getVersionedReferenceQueryName();
    Parameter<String, Object[], FetchQuery> paramToUse =
        this.paramResolver.resolve(refQueryName, fetchParam);
    Class<T> rcls = paramToUse.getResultClass();
    String sql = paramToUse.getScript();
    if (maxSize > 0) {
      sql = this.getDialect().getLimitSql(sql, 1, maxSize);
    }
    try {
      List<T> list = this.executor.select(sql, rcls, paramToUse.getConvertedParameters());
      BeanUtils.setProperty(obj, injectProName, list);
      this.fetch(list, paramToUse.getFetchQueries(), param);
    } catch (SQLException | IllegalAccessException | InvocationTargetException e) {
      throw new QueryRuntimeException(e);
    }
  }

  protected <T> void fetch(T e, List<FetchQuery> fetchQueries, Map<String, Object> param) {
    fetchQueries.stream().forEach(f -> this.fetch(e, f, new HashMap<>(param)));
  }

  protected Dialect getDialect() {
    return this.getSqlDataSource().getDialect();
  }

  protected abstract SqlDataSource getSqlDataSource();

  protected Map<String, Object> resolveFetchParam(Object obj, FetchQuery fetchQuery,
      Map<String, Object> param) {
    Map<String, Object> pmToUse = new HashMap<>();
    fetchQuery.getParameters().forEach(p -> {
      if (p.getSource() == FetchQueryParameterSource.P) {
        pmToUse.put(p.getName(), param.get(p.getName()));
      } else if (obj != null) {
        try {
          pmToUse.put(p.getName(), BeanUtils.getProperty(obj, p.getName()));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
          throw new QueryRuntimeException(
              String.format("Can not extract value from query result for fetch query param!",
                  fetchQuery.getReferenceQuery()));
        }
      }
    });
    return pmToUse;
  }
}

/*
 * Copyright (c) 2013-2018, Bingo.Chen (finesoft@gmail.com).
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
package org.asosat.ddd.application.query;

import java.util.HashMap;
import java.util.Map;
import org.corant.suites.query.shared.QueryParameter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * asosat-ddd
 *
 * @author bingo 下午7:13:42
 *
 */
public class GenericQueryParam<T> implements QueryParameter {

  private static final long serialVersionUID = 1128730276124512213L;

  private Map<String, Object> context = new HashMap<>();

  private Integer limit = 1;

  private Integer offset = 0;

  private T criteria;

  /**
   *
   * @return the context
   */
  @JsonIgnore
  @Override
  public Map<String, Object> getContext() {
    return context;
  }

  @Override
  public T getCriteria() {
    return criteria;
  }

  /**
   *
   * @return the limit
   */
  @Override
  public Integer getLimit() {
    return limit;
  }

  /**
   *
   * @return the offset
   */
  @Override
  public Integer getOffset() {
    return offset;
  }

  /**
   *
   * @param context the context to set
   */
  public GenericQueryParam<T> setContext(Map<String, Object> context) {
    this.context = context;
    return this;
  }

  /**
   *
   * @param criteria the criteria to set
   */
  public GenericQueryParam<T> setCriteria(T criteria) {
    this.criteria = criteria;
    return this;
  }

  /**
   *
   * @param limit the limit to set
   */
  public GenericQueryParam<T> setLimit(Integer limit) {
    this.limit = limit;
    return this;
  }

  /**
   *
   * @param offset the offset to set
   */
  public GenericQueryParam<T> setOffset(Integer offset) {
    this.offset = offset;
    return this;
  }

}

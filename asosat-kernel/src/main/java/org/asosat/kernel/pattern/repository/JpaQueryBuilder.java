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
package org.asosat.kernel.pattern.repository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Easy JPA query builder
 *
 * @author bingo 2013年4月23日
 */
public abstract class JpaQueryBuilder {

  ParameterBuilder parameterBuilder;

  /**
   * Build name query builder
   */
  public static JpaQueryBuilder namedQuery(final String namedQuery) {
    return new JpaQueryBuilder() {
      @Override
      public String toString() {
        return "Named: " + namedQuery + this.getParameterDescription();
      }

      @Override
      protected Query makeQueryObject(EntityManager entityManager) {
        return entityManager.createNamedQuery(namedQuery);
      }
    };
  }

  /**
   * Build native query with SQL statement builder
   */
  public static JpaQueryBuilder nativeQuery(final String nativeQuery) {
    return new JpaQueryBuilder() {
      @Override
      public String toString() {
        return "NativeQuery: " + nativeQuery + this.getParameterDescription();
      }

      @Override
      protected Query makeQueryObject(EntityManager entityManager) {
        return entityManager.createNativeQuery(nativeQuery);
      }
    };
  }

  /**
   * Build JPQL query builder
   */
  public static JpaQueryBuilder query(final String query) {
    return new JpaQueryBuilder() {
      @Override
      public String toString() {
        return "Query: " + query + " params: " + this.getParameterDescription();
      }

      @Override
      protected Query makeQueryObject(EntityManager entityManager) {
        return entityManager.createQuery(query);
      }
    };
  }

  /**
   * Build JPA query object
   */
  public Query createQuery(EntityManager entityManager) {
    Query query = this.makeQueryObject(entityManager);
    this.populateQuery(entityManager, query);
    return query;
  }

  /**
   * Set the parameter's collection to the builder.
   */
  public JpaQueryBuilder parameters(Collection<?> parameters) {
    return this.parameters(parameters == null ? null : parameters.toArray());
  }

  /**
   * Set the parameter's map to the builder.
   */
  public JpaQueryBuilder parameters(final Map<?, ?> parameterMap) {
    this.checkNoParametersConfigured();
    this.parameterBuilder = new ParameterBuilder() {
      @Override
      @SuppressWarnings("rawtypes")
      public void populateQuery(EntityManager entityManager, Query query) {
        if (parameterMap != null) {
          for (Entry entry : parameterMap.entrySet()) {
            query.setParameter(entry.getKey().toString(), entry.getValue());
          }
        }
      }

      @Override
      public String toString() {
        return "Parameters: " + parameterMap;
      }
    };
    return this;
  }

  /**
   * Set the parameter's array to the builder.
   */
  public JpaQueryBuilder parameters(final Object... parameters) {
    this.checkNoParametersConfigured();
    this.parameterBuilder = new ParameterBuilder() {
      @Override
      public void populateQuery(EntityManager entityManager, Query query) {
        if (parameters != null) {
          int counter = 0;
          for (Object parameter : parameters) {
            query.setParameter(counter++, parameter);
          }
        }
      }

      @Override
      public String toString() {
        return "Parameters: " + Arrays.toString(parameters);
      }
    };
    return this;
  }

  protected void checkNoParametersConfigured() {
    if (this.parameterBuilder != null) {
      throw new IllegalArgumentException(
          "Cannot add parameters to a QueryBuilder which already has parameters configured");
    }
  }

  protected String getParameterDescription() {
    if (this.parameterBuilder == null) {
      return "";
    } else {
      return " " + this.parameterBuilder.toString();
    }
  }

  protected abstract Query makeQueryObject(EntityManager entityManager);

  protected void populateQuery(EntityManager entityManager, Query query) {
    if (this.parameterBuilder != null) {
      this.parameterBuilder.populateQuery(entityManager, query);
    }
  }

  /**
   *
   * Inner parameter builder
   *
   * @author bingo 2013年4月27日
   * @since 1.0
   */
  protected abstract static class ParameterBuilder {
    public abstract void populateQuery(EntityManager entityManager, Query query);
  }

}

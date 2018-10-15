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

import static org.asosat.kernel.pattern.repository.JpaQueryBuilder.namedQuery;
import static org.asosat.kernel.util.MyBagUtils.isEmpty;
import static org.asosat.kernel.util.MyClsUtils.tryToLoadClassForName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import org.asosat.kernel.abstraction.Aggregate.AggregateIdentifier;
import org.asosat.kernel.abstraction.Being;
import org.asosat.kernel.abstraction.Entity;
import org.asosat.kernel.abstraction.Entity.EntityManagerProvider;
import org.asosat.kernel.annotation.stereotype.Repositories;
import org.asosat.kernel.util.JpaUtils;

/**
 * A simple encapsulation of JPA entity manager
 *
 * <b>Repositories Transaction Remark：</b>
 * <table border="1">
 * <tr>
 * <td><b>Transaction isolation level</b></td>
 * <td><b>Dirty reads</b></td>
 * <td><b>Non-repeatable reads </b></td>
 * <td><b>Phantom reads</b></td>
 * </tr>
 * <tr>
 * <td>READ_UNCOMMITTED</td>
 * <td>Allow</td>
 * <td>Allow</td>
 * <td>Allow</td>
 * </tr>
 * <tr>
 * <td>READ_COMMITTED</td>
 * <td>No Allow</td>
 * <td>Allow</td>
 * <td>Allow</td>
 * </tr>
 * <tr>
 * <td>REPEATABLE_READ</td>
 * <td>No Allow</td>
 * <td>No Allow</td>
 * <td>Allow</td>
 * </tr>
 * <tr>
 * <td>SERIALIZABLE</td>
 * <td>No Allow</td>
 * <td>No Allow</td>
 * <td>No Allow</td>
 * </tr>
 * </table>
 * <br/>
 * <table border="1">
 * <tr>
 * <td>Dirty reads:</td>
 * <td>A dirty read (aka uncommitted dependency) occurs when a transaction is allowed to read data
 * from a row that has been modified by another running transaction and not yet committed.</td>
 * </tr>
 * <tr>
 * <td>Non-repeatable reads:</td>
 * <td>A non-repeatable read occurs, when during the course of a transaction, a row is retrieved
 * twice and the values within the row differ between reads.</td>
 * </tr>
 * <tr>
 * <td>Phantom reads :</td>
 * <td>A phantom read occurs when, in the course of a transaction, new rows are added by another
 * transaction to the records being read.</td>
 * </tr>
 * </table>
 * <br/>
 * <a href = "https://en.wikipedia.org/wiki/Isolation_(database_systems)">Wikipedia Isolation</a>
 * <br/>
 *
 * @author bingo 2013年4月16日
 */

@Repositories
public abstract class AbstractJpaRepository implements JpaRepository {

  @Inject
  EntityManagerProvider entityManagerProvider;

  @Inject
  protected Logger logger;

  @Override
  public void clear() {
    this.getEntityManager().clear();
  }

  @Override
  public void detach(Object entity) {
    this.getEntityManager().detach(entity);
  }

  @Override
  public void evictCache(Class<?> entityClass) {
    Cache cache = this.getEntityManagerFactory().getCache();
    if (cache != null) {
      cache.evict(entityClass);
    } else {
      this.logger.warning("There is not cache mechanism!");
    }
  }

  @Override
  public void evictCache(Class<?> entityClass, Serializable id) {
    Cache cache = this.getEntityManagerFactory().getCache();
    if (cache != null) {
      cache.evict(entityClass, id);
    } else {
      this.logger.warning("There is not cache mechanism!");
    }
  }

  @Override
  public void evictCache(Entity entity) {
    if (entity == null || entity.getId() == null) {
      return;
    }
    this.evictCache(entity.getClass(), entity.getId());
  }

  /**
   * <b>Call this method with extreme caution</b>
   */
  @Override
  public void flush() {
    this.getEntityManager().flush();
  }

  @SuppressWarnings("unchecked")
  public <T> T get(AggregateIdentifier identifier) {
    if (identifier != null) {
      Class<?> cls = tryToLoadClassForName(identifier.getType());
      if (JpaUtils.isPersistenceClass(cls)) {
        return (T) this.getEntityManager().find(cls, identifier.getId());
      }
    }
    return null;
  }

  @Override
  public <T> T get(Class<T> entityClass, Serializable id) {
    return id != null ? this.getEntityManager().find(entityClass, id) : null;
  }

  public <T> T get(Class<T> entityClass, Serializable id, LockModeType lockMode) {
    return this.getEntityManager().find(entityClass, id, lockMode);
  }

  public <T> T get(Class<T> entityClass, Serializable id, LockModeType lockMode,
      Map<String, Object> properties) {
    return this.getEntityManager().find(entityClass, id, lockMode, properties);
  }

  public <T extends Being> T get(Class<T> entityClass, Serializable id, long evn) {
    T entity = this.get(entityClass, id);
    return entity != null && entity.getEvn().longValue() == evn ? entity : null;
  }

  public <T> T get(Class<T> entityClass, Serializable id, Map<String, Object> properties) {
    return this.getEntityManager().find(entityClass, id, properties);
  }

  public <T> T get(Query query) {
    List<T> result = this.select(query);
    if (!isEmpty(result)) {
      if (result.size() > 1) {
        this.logger.warning(
            "The query ['" + query + "'] result set record number > 1,may be breach intentions");
      }
      return result.get(0);
    }
    return null;
  }

  @Override
  public <T> T get(String queryName, Map<?, ?> param) {
    return this.get(namedQuery(queryName).parameters(param).createQuery(this.getEntityManager()));
  }

  @Override
  public <T> T get(String queryName, Object... param) {
    return this.get(namedQuery(queryName).parameters(param).createQuery(this.getEntityManager()));
  }

  /**
   * One transaction one entity manager
   */
  @Override
  public EntityManager getEntityManager() {
    return this.entityManagerProvider.getEntityManager();
  }

  public EntityManagerFactory getEntityManagerFactory() {
    return this.getEntityManager().getEntityManagerFactory();
  }

  @Override
  public <T> T getForUpdate(Class<T> entityClass, Serializable id) {
    return this.get(entityClass, id, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
  }

  public boolean isLoaded(Object object) {
    return object != null
        && this.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(object);
  }

  public void lock(Object object, LockModeType lockModeType) {
    if (this.getEntityManager().contains(object)) {
      this.getEntityManager().lock(object, lockModeType);
    }
  }

  public void lock(Object object, LockModeType lockModeType, Map<String, Object> properties) {
    if (this.getEntityManager().contains(object)) {
      this.getEntityManager().lock(object, lockModeType, properties);
    }
  }

  @Override
  public <T> T merge(T entity) {
    return this.getEntityManager().merge(entity);
  }

  @Override
  public <T> boolean persist(T entity) {
    this.getEntityManager().persist(entity);
    return true;
  }

  @Override
  public <T> boolean remove(T obj) {
    if (obj != null) {
      this.getEntityManager().remove(obj);
      return true;
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> List<T> select(Query query) {
    List<T> resultList = query.getResultList();
    if (resultList == null) {
      resultList = new ArrayList<>();
    }
    return resultList;
  }

  @Override
  public <T> List<T> select(String queryName, Map<?, ?> param) {
    return this
        .select(namedQuery(queryName).parameters(param).createQuery(this.getEntityManager()));
  }

  @Override
  public <T> List<T> select(String queryName, Object... param) {
    return this
        .select(namedQuery(queryName).parameters(param).createQuery(this.getEntityManager()));
  }

  public <R, T> List<T> selectAs(Query query, Function<R, T> mapper) {
    List<R> resultList = this.select(query);
    return resultList.stream().map(mapper).collect(Collectors.toList());
  }
}

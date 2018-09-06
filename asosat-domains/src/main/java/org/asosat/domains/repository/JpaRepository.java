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
package org.asosat.domains.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.asosat.kernel.abstraction.Entity;
import org.asosat.kernel.abstraction.Repository;

/**
 * @author bingo 下午8:34:30
 */
@org.asosat.domains.annotation.stereotype.Repositories
public interface JpaRepository extends Repository<Query> {

  /**
   * Clear the persistence context
   *
   * @see EntityManager#clear()
   */
  void clear();

  /**
   * Remove the given entity from the persistence context, causing a managed entity to become
   * detached.
   *
   * @see EntityManager#detach(Object)
   */
  void detach(Object entity);

  /**
   * @see Cache#evict(Class)
   */
  void evictCache(Class<?> entityClass);

  /**
   * @see Cache#evict(Class,id)
   */
  void evictCache(Class<?> entityClass, Serializable id);

  /**
   * @see #evictCache(Class, Serializable)
   */
  void evictCache(Entity entity);

  /**
   * Be careful to use this
   *
   * @see EntityManager#flush()
   */
  void flush();

  /**
   * Use named query and query parameter's map to retrieve an object.
   */
  <T> T get(String queryName, Map<?, ?> param);

  /**
   * Use named query and query parameter's array to retrieve an object.
   */
  <T> T get(String queryName, Object... param);

  /**
   * The entity manager
   */
  EntityManager getEntityManager();

  /**
   * Use named query and query parameter's map to retrieve object list.
   */
  <T> List<T> select(String queryName, Map<?, ?> param);

  /**
   * Use named query and query parameter's array to retrieve object list.
   */
  <T> List<T> select(String queryName, Object... param);

}

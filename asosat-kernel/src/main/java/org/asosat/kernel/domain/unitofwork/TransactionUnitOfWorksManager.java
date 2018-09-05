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
package org.asosat.kernel.domain.unitofwork;

import static org.asosat.kernel.domain.unitofwork.PkgMsgCds.ERR_UOW_CREATE;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import org.asosat.kernel.context.DefaultContext;
import org.asosat.kernel.domain.annotation.stereotype.InfrastructureServices;
import org.asosat.kernel.domain.repository.JpaUtils;
import org.asosat.kernel.exception.GeneralRuntimeException;

/**
 * @author bingo 下午3:45:08
 */
@ApplicationScoped
@InfrastructureServices
public abstract class TransactionUnitOfWorksManager extends AbstractUnitOfWorksManager {

  final Map<Transaction, TransactionUnitOfWorks> UOWS = new ConcurrentHashMap<>(512, 0.75f, 512);
  final Set<Class<?>> persistTypes = new CopyOnWriteArraySet<>();// FIXME

  public TransactionUnitOfWorksManager() {}

  public static TransactionUnitOfWorks currentUnitOfWorks() {
    return DefaultContext.bean(TransactionUnitOfWorksManager.class).getCurrentUnitOfWorks();
  }

  @Override
  public TransactionUnitOfWorks getCurrentUnitOfWorks() {
    try {
      final Transaction curTrans = this.getTransactionManager().getTransaction();
      return this.register(curTrans, (tx) -> {
        TransactionUnitOfWorks curUow = new TransactionUnitOfWorks(this, tx);
        this.getTransactionSynchronizationRegistry().registerInterposedSynchronization(curUow);
        return curUow;
      });
    } catch (Exception e) {
      throw new GeneralRuntimeException(e, ERR_UOW_CREATE);
    }
  }

  public abstract EntityManagerFactory getEntityManagerFactory();

  public abstract TransactionManager getTransactionManager();

  public abstract TransactionSynchronizationRegistry getTransactionSynchronizationRegistry();

  public TransactionUnitOfWorks getUnitOfWorks(Transaction key) {
    return this.UOWS.get(key);
  }

  public TransactionUnitOfWorks register(Transaction key,
      Function<Transaction, TransactionUnitOfWorks> func) {
    return this.UOWS.computeIfAbsent(key, func);
  }

  public TransactionUnitOfWorks unregister(Transaction key) {
    return this.UOWS.remove(key);
  }

  @PreDestroy
  void destroy() {
    this.UOWS.clear();
  }

  boolean isPersistType(Object object) {
    if (object == null) {
      return false;
    }
    Class<?> clazz = object.getClass();
    boolean persist = this.persistTypes.contains(clazz);
    if (!persist) {
      if (JpaUtils.isPersistenceClass(clazz)) {
        this.persistTypes.add(clazz);
        persist = true;
      }
    }
    return persist;
  }
}

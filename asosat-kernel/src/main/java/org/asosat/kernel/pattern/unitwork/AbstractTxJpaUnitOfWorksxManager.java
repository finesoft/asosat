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
package org.asosat.kernel.pattern.unitwork;

import static org.asosat.kernel.pattern.unitwork.PkgMsgCds.ERR_UOW_CREATE;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SynchronizationType;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import org.asosat.kernel.annotation.qualifier.Jpa;
import org.asosat.kernel.annotation.stereotype.InfrastructureServices;
import org.asosat.kernel.context.DefaultContext;
import org.asosat.kernel.exception.GeneralRuntimeException;

/**
 * @author bingo 下午3:45:08
 */
@Jpa
@ApplicationScoped
@InfrastructureServices
public abstract class AbstractTxJpaUnitOfWorksxManager extends AbstractUnitOfWorksManager {

  public static DefaultTxJpaUnitOfWorks currentUnitOfWorks() {
    return DefaultContext.bean(AbstractTxJpaUnitOfWorksxManager.class).getCurrentUnitOfWorks();
  }

  final Map<Transaction, DefaultTxJpaUnitOfWorks> UOWS = new ConcurrentHashMap<>(512, 0.75f, 512);

  public AbstractTxJpaUnitOfWorksxManager() {}

  @Override
  public DefaultTxJpaUnitOfWorks getCurrentUnitOfWorks() {
    try {
      final Transaction curTx = this.getTransactionManager().getTransaction();
      final EntityManager curEm =
          this.getEntityManagerFactory().createEntityManager(SynchronizationType.SYNCHRONIZED);
      return this.register(curTx, (tx) -> {
        DefaultTxJpaUnitOfWorks curUow = new DefaultTxJpaUnitOfWorks(this, curEm, tx);
        this.getTransactionSynchronizationRegistry().registerInterposedSynchronization(curUow);
        return curUow;
      });
    } catch (Exception e) {
      throw new GeneralRuntimeException(e, ERR_UOW_CREATE);
    }
  }

  @Override
  public EntityManager getEntityManager() {
    return this.getCurrentUnitOfWorks().getEntityManager();
  }

  public abstract EntityManagerFactory getEntityManagerFactory();

  public abstract TransactionManager getTransactionManager();

  public abstract TransactionSynchronizationRegistry getTransactionSynchronizationRegistry();

  public DefaultTxJpaUnitOfWorks getUnitOfWorks(Transaction key) {
    return this.UOWS.get(key);
  }

  public DefaultTxJpaUnitOfWorks register(Transaction key,
      Function<Transaction, DefaultTxJpaUnitOfWorks> func) {
    return this.UOWS.computeIfAbsent(key, func);
  }

  public DefaultTxJpaUnitOfWorks unregister(Transaction key) {
    return this.UOWS.remove(key);
  }

  @Override
  void clearCurrentUnitOfWorks(Object key) {
    this.UOWS.remove(key);
  }

  @PreDestroy
  void destroy() {
    this.UOWS.clear();
  }

}
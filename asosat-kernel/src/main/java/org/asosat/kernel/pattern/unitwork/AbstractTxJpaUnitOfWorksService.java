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
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SynchronizationType;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import org.asosat.kernel.annotation.stereotype.InfrastructureServices;
import org.asosat.kernel.context.DefaultContext;
import org.asosat.kernel.exception.GeneralRuntimeException;

/**
 * asosat-kernel
 *
 * @author bingo 上午9:35:01
 *
 */
@ApplicationScoped
@InfrastructureServices
public abstract class AbstractTxJpaUnitOfWorksService extends AbstractUnitOfWorksService {

  ThreadLocal<DefaultTxJpaUnitOfWorks> UOWS;

  public AbstractTxJpaUnitOfWorksService() {}

  public static DefaultTxJpaUnitOfWorks currentUnitOfWorks() {
    return DefaultContext.bean(AbstractTxJpaUnitOfWorksxService.class).getCurrentUnitOfWorks();
  }

  @Override
  public DefaultTxJpaUnitOfWorks getCurrentUnitOfWorks() {
    return this.UOWS.get();
  }

  @Override
  public EntityManager getEntityManager() {
    return this.getCurrentUnitOfWorks().getEntityManager();
  }

  public abstract EntityManagerFactory getEntityManagerFactory();

  public abstract TransactionManager getTransactionManager();

  public abstract TransactionSynchronizationRegistry getTransactionSynchronizationRegistry();

  @Override
  void clearCurrentUnitOfWorks(Object key) {
    this.UOWS.remove();
  }

  @PreDestroy
  void destroy() {
    this.clearCurrentUnitOfWorks(null);
  }

  @PostConstruct
  void init() {
    this.UOWS = ThreadLocal.withInitial(() -> {
      try {
        final Transaction tx = this.getTransactionManager().getTransaction();
        final EntityManager em =
            this.getEntityManagerFactory().createEntityManager(SynchronizationType.SYNCHRONIZED);
        DefaultTxJpaUnitOfWorks uow =
            new DefaultTxJpaUnitOfWorks(AbstractTxJpaUnitOfWorksService.this, em, tx);
        this.getTransactionSynchronizationRegistry().registerInterposedSynchronization(uow);
        return uow;
      } catch (SystemException e) {
        throw new GeneralRuntimeException(e, ERR_UOW_CREATE);
      }
    });
  }

}

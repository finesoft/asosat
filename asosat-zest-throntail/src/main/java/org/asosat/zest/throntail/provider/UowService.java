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
package org.asosat.zest.throntail.provider;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import org.asosat.kernel.pattern.unitwork.AbstractTxJpaUnitOfWorksxService;
import org.asosat.kernel.stereotype.InfrastructureServices;

/**
 * @author bingo 上午11:27:31
 *
 */
@InfrastructureServices
@ApplicationScoped
public class UowService extends AbstractTxJpaUnitOfWorksxService {

  @Inject
  TransactionManager transactionManager;

  @Inject
  TransactionSynchronizationRegistry transactionSynchronizationRegistry;

  @PersistenceUnit(name = "examplePu")
  EntityManagerFactory entityManagerFactory;

  public UowService() {}

  @Override
  public EntityManagerFactory getEntityManagerFactory() {
    return this.entityManagerFactory;
  }

  @Override
  public TransactionManager getTransactionManager() {
    return this.transactionManager;
  }

  @Override
  public TransactionSynchronizationRegistry getTransactionSynchronizationRegistry() {
    return this.transactionSynchronizationRegistry;
  }

}

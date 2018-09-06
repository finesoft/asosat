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
package org.asosat.domains.unitofwork;

import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.asosat.domains.annotation.stereotype.InfrastructureServices;
import org.asosat.domains.message.MessageService;
import org.asosat.domains.saga.SagaService;

/**
 * @author bingo 下午7:19:46
 *
 */
@ApplicationScoped
@InfrastructureServices
public abstract class AbstractUnitOfWorksManager implements UnitOfWorksManager {

  @Inject
  Instance<UnitOfWorksListener> listeners;

  @Inject
  Instance<UnitOfWorksHandler> handlers;

  @Inject
  MessageService messageService;

  @Inject
  SagaService sagaService;

  public AbstractUnitOfWorksManager() {}

  @Override
  public Stream<UnitOfWorksHandler> getHandlers() {
    return this.handlers == null || !this.handlers.isResolvable() ? Stream.empty()
        : this.handlers.stream();
  }

  @Override
  public Stream<UnitOfWorksListener> getListeners() {
    return this.listeners == null || !this.handlers.isResolvable() ? Stream.empty()
        : this.listeners.stream();
  }

  @Override
  public MessageService getMessageService() {
    return this.messageService;
  }

  @Override
  public SagaService getSagaService() {
    return this.sagaService;
  }


}
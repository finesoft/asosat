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
package org.asosat.domain.saga;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.asosat.kernel.abstraction.Message;
import org.asosat.kernel.abstraction.PersistenceService;
import org.asosat.kernel.abstraction.Saga;
import org.asosat.kernel.abstraction.SagaService;
import org.asosat.kernel.annotation.MessageQueue.MessageQueueLiteral;
import org.asosat.kernel.exception.NotSupportedException;
import org.asosat.kernel.stereotype.InfrastructureServices;
import org.asosat.kernel.util.JpaUtils;

/**
 * asosat-kernel
 *
 * @author bingo 上午1:02:32
 *
 */
@ApplicationScoped
@InfrastructureServices
public class DefaultSagaService implements SagaService {

  @Inject
  protected PersistenceService persistenceService;

  @Inject
  @Any
  protected Instance<SagaManager> sagaManagers;

  protected final Map<Class<?>, Boolean> persistSagaClasses =
      new ConcurrentHashMap<>(256, 0.75f, 256);

  @Override
  public Stream<SagaManager> getManagers(Annotation... annotations) {
    Instance<SagaManager> inst = this.sagaManagers.select(annotations);
    if (inst.isResolvable()) {
      return inst.stream();
    } else {
      return Stream.empty();
    }
  }

  @Transactional
  @Override
  public void trigger(Message message) {
    this.getManagers(MessageQueueLiteral.of(message.queueName())).forEach(sm -> {
      Saga saga = sm.begin(message);
      if (this.persistSagaClasses.computeIfAbsent(saga.getClass(),
          JpaUtils::isPersistenceEntityClass)) {
        this.persistenceService.persist(saga, true);
      } else {
        throw new NotSupportedException();
      }
    });
  }

}
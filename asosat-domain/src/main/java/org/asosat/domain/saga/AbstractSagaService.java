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
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.asosat.kernel.abstraction.Message;
import org.asosat.kernel.annotation.qualifier.MessageQueue.MessageQueueLiteral;
import org.asosat.kernel.annotation.stereotype.InfrastructureServices;
import org.asosat.kernel.pattern.saga.Saga;
import org.asosat.kernel.pattern.saga.SagaService;

/**
 * asosat-kernel
 *
 * @author bingo 上午1:02:32
 *
 */
@ApplicationScoped
@InfrastructureServices
public abstract class AbstractSagaService implements SagaService {

  @Inject
  @Any
  protected Instance<SagaManager> sagaManagers;


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
      this.persist(saga);
    });
  }

}

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
package org.asosat.domain.message;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.asosat.kernel.context.DefaultContext;
import org.asosat.kernel.supertype.Message;
import org.asosat.domain.annotation.qualifier.MessageQueue.MessageQueueLiteral;
import org.asosat.domain.annotation.stereotype.InfrastructureServices;
import org.asosat.domain.repository.JpaRepository;

/**
 * @author bingo 上午11:38:09
 *
 */
@ApplicationScoped
@InfrastructureServices
public class DefaultExchangeMessageHandler implements ExchangedMessageHandler {

  @Inject
  protected JpaRepository repo;

  @Inject
  protected MessageConvertor convertor;

  public DefaultExchangeMessageHandler() {}

  @Override
  @Transactional
  public void handle(@ObservesAsync ExchangedMessage message) {
    if (null == message || null == message.queueName()) {
      return;
    }
    MessageQueueLiteral qualifier = MessageQueueLiteral.of(message.queueName());
    Message persistMessage = this.convertor.from(message);
    if (persistMessage != null) {
      this.repo.persist(persistMessage);
      this.repo.detach(persistMessage);
      DefaultContext.event().select(qualifier).fireAsync(persistMessage);
    } else {
      DefaultContext.event().select(qualifier).fireAsync(new DefaultTransientMessage(message));
    }
  }

}

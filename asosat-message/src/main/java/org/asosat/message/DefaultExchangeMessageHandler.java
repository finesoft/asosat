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
package org.asosat.message;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.asosat.kernel.abstraction.Message;
import org.asosat.kernel.abstraction.Message.ExchangedMessage;
import org.asosat.kernel.abstraction.MessageService.MessageConvertor;
import org.asosat.kernel.abstraction.MessageService.MessageStroage;
import org.asosat.kernel.annotation.qualifier.MessageQueue.MessageQueueLiteral;
import org.asosat.kernel.annotation.stereotype.InfrastructureServices;
import org.asosat.kernel.context.DefaultContext;

/**
 * @author bingo 上午11:38:09
 *
 */
@ApplicationScoped
@InfrastructureServices
public class DefaultExchangeMessageHandler implements ExchangedMessageHandler {

  @Inject
  protected MessageStroage stroage;

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
      this.stroage.store(persistMessage);
      DefaultContext.event().select(qualifier).fireAsync(persistMessage);
    } else {
      DefaultContext.event().select(qualifier).fireAsync(new DefaultTransientMessage(message));
    }
  }

}

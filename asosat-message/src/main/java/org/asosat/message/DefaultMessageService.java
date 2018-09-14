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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.asosat.kernel.abstraction.Message;
import org.asosat.kernel.abstraction.Message.ExchangedMessage;
import org.asosat.kernel.abstraction.MessageService;
import org.asosat.kernel.abstraction.PersistenceService;
import org.asosat.kernel.exception.GeneralRuntimeException;
import org.asosat.kernel.pattern.interceptor.Asynchronous;
import org.asosat.kernel.stereotype.InfrastructureServices;
import org.asosat.kernel.util.JpaUtils;

/**
 * @author bingo 上午10:51:18
 *
 */
@ApplicationScoped
@InfrastructureServices
public class DefaultMessageService implements MessageService {

  @Inject
  protected Logger logger;

  @Inject
  protected PersistenceService persistenceService;

  @Inject
  protected MessageConvertor convertor;

  @Inject
  protected ExchangedMessageHandler exchangeMessageHandler;

  @Inject
  protected MessageSender sender;

  protected final Map<Class<?>, Boolean> persistMessageClasses =
      new ConcurrentHashMap<>(256, 0.75f, 256);

  public DefaultMessageService() {}

  @Override
  public MessageConvertor getConvertor() {
    return this.convertor;
  }

  @Transactional
  @Override
  public Message persist(Message message) {
    if (this.persistMessageClasses.computeIfAbsent(message.getClass(),
        JpaUtils::isPersistenceEntityClass)) {
      this.persistenceService.persist(message, true);
    }
    return message;
  }

  @Asynchronous(fair = false)
  @Override
  public void receive(ExchangedMessage msg) {
    if (msg != null) {
      this.exchangeMessageHandler.handle(msg);
    } else {
      this.logger.log(Level.WARNING, "Can not find exchanged message handler!");
    }
  }

  @Asynchronous(fair = false) // FIXME ordered
  @Override
  public void send(ExchangedMessage msg) {
    if (msg != null) {
      try {
        this.sender.send(msg);
      } catch (Exception e) {
        throw new GeneralRuntimeException(e, "");// FIXME MSG
      }
    } else {
      this.logger.log(Level.WARNING, "Can not find message channel!");
    }
  }
}

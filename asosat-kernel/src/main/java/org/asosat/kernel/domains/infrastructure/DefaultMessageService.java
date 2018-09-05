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
package org.asosat.kernel.domains.infrastructure;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.asosat.kernel.domains.annotation.stereotype.InfrastructureServices;
import org.asosat.kernel.domains.message.ExchangedMessage;
import org.asosat.kernel.domains.message.ExchangedMessageHandler;
import org.asosat.kernel.domains.message.MessageConvertor;
import org.asosat.kernel.domains.message.MessageSender;
import org.asosat.kernel.domains.message.MessageService;
import org.asosat.kernel.exception.GeneralRuntimeException;
import org.asosat.kernel.pattern.interceptor.Asynchronous;

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
  protected MessageConvertor convertor;


  @Inject
  protected ExchangedMessageHandler exchangeMessageHandler;

  @Inject
  protected MessageSender sender;

  public DefaultMessageService() {}

  @Override
  public MessageConvertor getConvertor() {
    return this.convertor;
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
        throw new GeneralRuntimeException(e, PkgMsgCds.ERR_MSG_SERV_SEND);
      }
    } else {
      this.logger.log(Level.WARNING, "Can not find message channel!");
    }
  }
}

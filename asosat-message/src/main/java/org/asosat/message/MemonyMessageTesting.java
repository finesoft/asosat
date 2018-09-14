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

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.asosat.kernel.abstraction.Message.ExchangedMessage;
import org.asosat.kernel.abstraction.MessageService;
import org.asosat.kernel.context.DefaultContext;
import org.asosat.kernel.exception.GeneralRuntimeException;
import org.asosat.kernel.resource.GlobalMessageCodes;

/**
 * @author bingo 下午6:10:11
 *
 */
public abstract class MemonyMessageTesting {

  private static Logger logger = Logger.getLogger(MemonyMessageTesting.class.getName());

  private static Random rd = new Random();

  public static void test(ExchangedMessage msg) {
    logger.fine(String.format("Send message [%s] [%s] [%s] to globale bus!", msg.queueName(),
        msg.getOriginalMessage().getId(), msg.getPayload().toString()));
    CompletableFuture.runAsync(() -> {
      try {
        Thread.sleep(Long.valueOf(rd.nextInt(1000) % (501) + 50));
      } catch (InterruptedException e) {
      }
    }).whenCompleteAsync((r, e) -> {
      if (e != null) {
        throw new GeneralRuntimeException(e, GlobalMessageCodes.ERR_SYS);
      }
      logger.fine(String.format("Receive message [%s] [%s] [%s] from globale bus!", msg.queueName(),
          msg.getOriginalMessage().getId(), msg.getPayload().toString()));
      DefaultContext.bean(MessageService.class).receive(msg);
    });
  }

}

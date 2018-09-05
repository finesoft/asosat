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
package org.asosat.kernel.domains.message;

import java.util.Objects;
import java.util.Queue;
import org.asosat.kernel.abstraction.Message;

/**
 * Marking a integration message object can be merged. Imagine that in the transaction unit of works
 * if an aggregate property value from '1' change to '2' and finally change to '1' then it should
 * not raise property changed message, this is experimental.
 *
 * @author bingo 下午3:37:07
 *
 */
public interface MergableMessage extends Message {

  static boolean isCorrelated(Message m, Message o) {
    return m instanceof MergableMessage && o instanceof MergableMessage
        && Objects.equals(m.getClass(), o.getClass())
        && Objects.equals(m.queueName(), o.queueName())
        && Objects.equals(m.sourceObject(), o.sourceObject());
  }

  static void mergeToQueue(Queue<Message> queue, MergableMessage msg) {
    MergableMessage newMgbMsg = msg, oldMgbMsg = null;
    for (Message queMsg : queue) {
      if (MergableMessage.isCorrelated(queMsg, newMgbMsg)) {
        oldMgbMsg = (MergableMessage) queMsg;
        break;
      }
    }
    if (oldMgbMsg == null || !newMgbMsg.canMerge(oldMgbMsg)) {
      queue.add(newMgbMsg);
    } else {
      queue.remove(oldMgbMsg);
      if (newMgbMsg.merge(oldMgbMsg).isValid()) {
        queue.add(newMgbMsg);
      }
    }
  }

  default boolean canMerge(MergableMessage other) {
    return true;
  }

  default boolean isValid() {
    return true;
  }

  MergableMessage merge(MergableMessage other);

}

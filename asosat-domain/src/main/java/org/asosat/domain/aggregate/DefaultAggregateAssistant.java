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
package org.asosat.domain.aggregate;

import static org.asosat.domain.aggregate.PkgMsgCds.ERR_AGG_AST_INSTAL;
import static org.asosat.domain.aggregate.PkgMsgCds.ERR_AGG_MSG_SEQ;
import static org.asosat.kernel.util.Preconditions.requireGaet;
import static org.asosat.kernel.util.Preconditions.requireNotNull;
import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.asosat.kernel.abstraction.Aggregate;
import org.asosat.kernel.abstraction.Event;
import org.asosat.kernel.abstraction.Message;
import org.asosat.kernel.context.DefaultContext;

/**
 * @author bingo 上午10:57:03
 */
public class DefaultAggregateAssistant implements AggregateAssistant {

  static final String FIRE_LOG = "Fire event [%s] to event listener!";
  static final String RISE_LOG = "Register integration message [%s] to message queue!";

  protected final transient Logger logger = Logger.getLogger(this.getClass().toString());

  protected transient final Aggregate aggregate;
  protected transient final Queue<Message> messages = new LinkedList<>();
  protected transient volatile long lastMessageSequenceNumber = -1L;

  public DefaultAggregateAssistant(Aggregate aggregate) {
    this.aggregate = requireNotNull(aggregate, ERR_AGG_AST_INSTAL);
    this.lastMessageSequenceNumber = aggregate.getEvn();
  }

  public DefaultAggregateAssistant(Aggregate aggregate, long lastMessageSequenceNumber) {
    this(aggregate);
    this.lastMessageSequenceNumber = requireGaet(lastMessageSequenceNumber, 0L, ERR_AGG_MSG_SEQ,
        aggregate.toHumanReader(Locale.getDefault()));
  }

  @Override
  public void clearMessages() {
    this.messages.clear();
  }

  @Override
  public List<Message> dequeueMessages(boolean flush) {
    final AtomicLong counter = new AtomicLong(this.lastMessageSequenceNumber);
    List<Message> exMsgs = this.messages.stream().map(m -> {
      m.getMetadata().resetSequenceNumber(counter.incrementAndGet());
      return m;
    }).collect(Collectors.toList());
    if (flush) {
      this.lastMessageSequenceNumber += exMsgs.size();
      this.clearMessages();
    }
    return exMsgs;
  }

  @Override
  public void enqueueMessages(Message... messages) {
    if (this.aggregate.getId() != null) {
      for (Message msg : messages) {
        if (msg != null) {
          this.logger.log(Level.FINE, String.format(RISE_LOG, msg.toString()));
          if (msg instanceof MergableMessage) {
            MergableMessage.mergeToQueue(this.messages, (MergableMessage) msg);
          } else {
            this.messages.add(msg);
          }
        }
      }
      // FIXME
      if (this.aggregate instanceof AbstractDefaultAggregate) {
        AbstractDefaultAggregate.class.cast(this.aggregate)
            .setMsn(this.lastMessageSequenceNumber + this.messages.size());
      }
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    DefaultAggregateAssistant other = (DefaultAggregateAssistant) obj;
    if (this.aggregate == null) {
      if (other.aggregate != null) {
        return false;
      }
    } else if (!this.aggregate.equals(other.aggregate)) {
      return false;
    }
    return true;
  }

  @Override
  public void fireAsyncEvent(Event event, Annotation... qualifiers) {
    if (event != null) {
      this.logger.log(Level.FINE, String.format(FIRE_LOG, event.toString()));
      DefaultContext.fireAsyncEvent(event, qualifiers);
    }
  }

  @Override
  public void fireEvent(Event event, Annotation... qualifiers) {
    if (event != null) {
      this.logger.log(Level.FINE, String.format(FIRE_LOG, event.toString()));
      DefaultContext.fireEvent(event, qualifiers);
    }
  }

  @Override
  public Aggregate getAggregate() {
    return this.aggregate;
  }

  public long getLastMessageSequenceNumber() {
    return this.lastMessageSequenceNumber;
  }

  @Override
  public long getMessageSequenceNumber() {
    return this.messages.size() + this.getLastMessageSequenceNumber();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (this.aggregate == null ? 0 : this.aggregate.hashCode());
    return result;
  }

  protected void setLastMessageSequenceNumber(long lastMessageSequenceNumber) {
    this.lastMessageSequenceNumber = lastMessageSequenceNumber;
  }
}

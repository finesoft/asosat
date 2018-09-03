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

import java.util.List;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import org.asosat.kernel.supertype.Message;

/**
 * An abstract aggregate that part of the implementation of event processing and other related
 * functions, use JPA.
 *
 * @author bingo 下午3:43:12
 */
@MappedSuperclass
public abstract class AbstractDefaultAggregate extends AbstractAggregate {

  private static final long serialVersionUID = -1347035224644784732L;

  @Column(name = "msgSeqNum")
  private volatile long msgSeqNum = 0L;

  protected AbstractDefaultAggregate() {}

  @Override
  public synchronized List<Message> extractMessages(boolean flush) {
    List<Message> events = super.extractMessages(flush);
    if (flush) {
      this.setMsgSeqNum(this.msgSeqNum + events.size());
    }
    return events;
  }

  public synchronized long getMsgSeqNum() {
    return this.msgSeqNum;
  }

  @Override
  protected synchronized AggregateAssistant assistant() {
    if (this.assistant == null) {
      this.assistant = new DefaultAggregateAssistant(this, this.msgSeqNum);
    }
    return this.assistant;
  }

  protected synchronized void setMsgSeqNum(long msgSeqNum) {
    this.msgSeqNum = msgSeqNum;
  }

}

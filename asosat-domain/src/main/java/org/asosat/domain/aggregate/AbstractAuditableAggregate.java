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
import java.util.stream.Stream;
import javax.persistence.MappedSuperclass;
import org.asosat.kernel.abstraction.Message;

/**
 * @author bingo 下午3:44:36
 *
 */
@MappedSuperclass
public abstract class AbstractAuditableAggregate extends AbstractAggregate {

  private static final long serialVersionUID = 3636641230618671037L;

  public AbstractAuditableAggregate() {}

  public AbstractAuditableAggregate(Stream<? extends Message> messageStream) {}

  @Override
  public synchronized List<Message> extractMessages(boolean flush) {
    List<Message> events = super.extractMessages(flush);
    if (flush && !events.isEmpty()) {
      this.setEvoVerNum(this.getEvoVerNum() + events.size());
    }
    return events;
  }

}

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

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import org.asosat.kernel.abstraction.Entity;
import org.asosat.kernel.abstraction.Message.MessageHandling;

/**
 * @author bingo 下午4:53:25
 *
 */
@MappedSuperclass
public abstract class AbstractMessageHandling implements MessageHandling, Entity {

  private static final long serialVersionUID = -4398613121353443886L;

  @Column
  private boolean success;

  @Column
  private Instant handledTime;

  @Column
  private String queue;

  public AbstractMessageHandling() {}

  @Override
  public Instant getHandledTime() {
    return this.handledTime;
  }

  @Override
  public String getQueue() {
    return this.queue;
  }

  @Override
  public boolean isSuccess() {
    return this.success;
  }

  protected void setHandledTime(Instant handledTime) {
    this.handledTime = handledTime;
  }

  protected void setQueue(String queue) {
    this.queue = queue;
  }

  protected void setSuccess(boolean success) {
    this.success = success;
  }

}

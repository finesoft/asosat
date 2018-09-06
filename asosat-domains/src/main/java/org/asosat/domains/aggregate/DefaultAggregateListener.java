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
package org.asosat.domains.aggregate;

import java.util.logging.Logger;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import org.asosat.domains.unitofwork.TransactionUnitOfWorks;
import org.asosat.domains.unitofwork.TransactionUnitOfWorksManager;
import org.asosat.domains.unitofwork.UnitOfWorks;
import org.asosat.domains.unitofwork.UnitOfWorksManager;
import org.asosat.kernel.abstraction.Lifecycle;
import org.asosat.kernel.context.DefaultContext;

/**
 * Global aggregate persistence listener use for unit of work
 *
 * @see TransactionUnitOfWorksManager
 * @see TransactionUnitOfWorks
 * @author bingo 下午12:06:07
 */
public class DefaultAggregateListener {

  protected final transient Logger logger = Logger.getLogger(this.getClass().toString());

  public DefaultAggregateListener() {}

  void handlePostLoad(AbstractAggregate o) {
    o.initLifecycle(Lifecycle.ENABLED);
    o.assistant().clearMessages();
  }

  void handlePostPersist(AbstractAggregate o) {
    o.initLifecycle(Lifecycle.ENABLED);
    this.registerToUnitOfWork(o);
    o.assistant().clearMessages();
  }

  void handlePostRemove(AbstractAggregate o) {
    o.initLifecycle(Lifecycle.DESTROYED);
    this.registerToUnitOfWork(o);
    o.assistant().clearMessages();
  }

  void handlePostUpdate(AbstractAggregate o) {
    o.assistant().clearMessages();
  }

  void handlePrePersist(AbstractAggregate o) {}

  void handlePreRemove(AbstractAggregate o) {}

  void handlePreUpdate(AbstractAggregate o) {
    o.initLifecycle(Lifecycle.ENABLED);
    this.registerToUnitOfWork(o);
  }

  @PostLoad
  void onPostLoad(Object o) {
    if (o instanceof AbstractAggregate) {
      this.handlePostLoad((AbstractAggregate) o);
    }
  }

  @PostPersist
  void onPostPersist(Object o) {
    if (o instanceof AbstractAggregate) {
      this.handlePostPersist((AbstractAggregate) o);
    }
  }

  @PostRemove
  void onPostRemove(Object o) {
    if (o instanceof AbstractAggregate) {
      this.handlePostRemove((AbstractAggregate) o);
    }
  }

  @PostUpdate
  void onPostUpdate(Object o) {
    if (o instanceof AbstractAggregate) {
      this.handlePostUpdate((AbstractAggregate) o);
    }
  }

  @PrePersist
  void onPrePersist(Object o) {
    if (o instanceof AbstractAggregate) {
      this.handlePrePersist((AbstractAggregate) o);
    }
  }

  @PreRemove
  void onPreRemove(Object o) {
    if (o instanceof AbstractAggregate) {
      this.handlePreRemove((AbstractAggregate) o);
    }
  }

  @PreUpdate
  void onPreUpdate(Object o) {
    if (o instanceof AbstractAggregate) {
      this.handlePreUpdate((AbstractAggregate) o);
    }
  }

  void registerToUnitOfWork(AbstractAggregate o) {
    UnitOfWorks curUow = DefaultContext.bean(UnitOfWorksManager.class).getCurrentUnitOfWorks();
    if (curUow != null) {
      curUow.register(o);
    }
  }

}

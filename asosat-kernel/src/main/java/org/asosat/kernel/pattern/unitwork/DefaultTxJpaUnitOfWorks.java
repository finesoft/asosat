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
package org.asosat.kernel.pattern.unitwork;

import static org.asosat.kernel.pattern.unitwork.PkgMsgCds.ERR_UOW_NOT_ACT;
import static org.asosat.kernel.pattern.unitwork.PkgMsgCds.ERR_UOW_TRANS;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import org.asosat.kernel.abstraction.Aggregate;
import org.asosat.kernel.abstraction.Aggregate.AggregateIdentifier;
import org.asosat.kernel.abstraction.Aggregate.DefaultAggregateIdentifier;
import org.asosat.kernel.abstraction.Lifecycle;
import org.asosat.kernel.abstraction.Message;
import org.asosat.kernel.exception.GeneralRuntimeException;

/**
 * @author bingo 上午9:34:06
 */
public class DefaultTxJpaUnitOfWorks extends AbstractUnitOfWorks implements Synchronization {
  static final String BGN_LOG = "Begin unit of work [%s]";
  static final String END_LOG = "End unit of work [%s].";
  final transient Transaction transaction;
  final transient EntityManager entityManager;
  final Map<Lifecycle, Set<AggregateIdentifier>> registration = new EnumMap<>(Lifecycle.class);

  protected DefaultTxJpaUnitOfWorks(AbstractUnitOfWorksManager manager, EntityManager entityManager,
      Transaction transaction) {
    super(manager);
    this.transaction = transaction;
    this.entityManager = entityManager;
    Arrays.stream(Lifecycle.values()).forEach(e -> this.registration.put(e, new LinkedHashSet<>()));
    this.logger.debug(() -> String.format(BGN_LOG, transaction.toString()));
  }

  @Override
  public void afterCompletion(int status) {
    final boolean success = status == Status.STATUS_COMMITTED;
    try {
      this.complete(success);
    } finally {
      final Map<Lifecycle, Set<AggregateIdentifier>> cloneRegistration = this.getRegistration();
      this.clear();
      this.logger.debug(() -> String.format(END_LOG, this.transaction.toString()));
      this.handlePostCompleted(cloneRegistration, success);
    }
  }

  @Override
  public void beforeCompletion() {
    this.handlePreComplete();
    this.handleMessage();
    this.entityManager.flush();// FIXME
  }

  public EntityManager getEntityManager() {
    return this.entityManager;
  }

  @Override
  public Transaction getId() {
    return this.transaction;
  }

  @Override
  public Map<Lifecycle, Set<AggregateIdentifier>> getRegistration() {
    Map<Lifecycle, Set<AggregateIdentifier>> clone = new EnumMap<>(Lifecycle.class);
    this.registration.forEach((k, v) -> {
      clone.put(k, Collections.unmodifiableSet(new LinkedHashSet<>(v)));
    });
    return Collections.unmodifiableMap(clone);
  }

  public boolean isInTransaction() {
    try {
      if (this.transaction == null) {
        return false;
      } else {
        int status = this.transaction.getStatus();
        return status == Status.STATUS_ACTIVE || status == Status.STATUS_COMMITTING
            || status == Status.STATUS_MARKED_ROLLBACK || status == Status.STATUS_PREPARED
            || status == Status.STATUS_PREPARING || status == Status.STATUS_ROLLING_BACK;
      }
    } catch (SystemException e) {
      throw new GeneralRuntimeException(e, ERR_UOW_TRANS);
    }
  }

  @Override
  public void register(Object obj) {
    if (this.activated && this.isInTransaction()) {
      if (obj instanceof Aggregate) {
        Aggregate aggregate = (Aggregate) obj;
        if (aggregate.getId() != null) {
          AggregateIdentifier ai = new DefaultAggregateIdentifier(aggregate);
          this.registration.forEach((k, v) -> {
            if (k != aggregate.getLifecycle()) {
              v.remove(ai);
            }
          });
          this.registration.get(aggregate.getLifecycle()).add(ai);
          this.message.addAll(aggregate.extractMessages(true));
        }
      } else if (obj instanceof Message) {
        this.message.add((Message) obj);
      }
    } else {
      throw new GeneralRuntimeException(ERR_UOW_NOT_ACT);
    }
  }

  @Override
  public String toString() {
    return this.transaction.toString();
  }

  @Override
  public void unregister(Object obj) {
    if (this.activated) {
      if (obj instanceof Aggregate) {
        Aggregate aggregate = (Aggregate) obj;
        if (aggregate.getId() != null) {
          AggregateIdentifier ai = new DefaultAggregateIdentifier(aggregate);
          this.registration.values().forEach(v -> v.remove(ai));
          this.message.removeIf(e -> Objects.equals(e.getMetadata().getSource(), ai));
        }
      } else if (obj instanceof Message) {
        this.message.remove(obj);
      }
    } else {
      throw new GeneralRuntimeException(ERR_UOW_NOT_ACT);
    }
  }

  @Override
  protected void clear() {
    if (this.entityManager.isOpen()) {
      this.entityManager.close();
    }
    this.registration.clear();
    this.getManager().clearCurrentUnitOfWorks(this.transaction);
    super.clear();
  }

  @Override
  protected AbstractUnitOfWorksManager getManager() {
    return (AbstractUnitOfWorksManager) super.getManager();
  }

  protected void handleMessage() {
    this.message.stream().sorted(Message::compare).map(this.messageService::store)
        .forEachOrdered(this.sagaService::trigger);
  }
}

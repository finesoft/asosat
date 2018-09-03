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
package org.asosat.domain.service;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import org.asosat.domain.aggregate.DefaultAggregateListener.LifcyclePhase;
import org.asosat.domain.annotation.stereotype.InfrastructureServices;
import org.asosat.domain.event.LifecycleEvent;
import org.asosat.domain.repository.JpaRepository;


@ApplicationScoped
@InfrastructureServices
public class DefaultPersistenceService implements PersistenceService {

  @Inject
  protected JpaRepository repo;

  @Override
  public void merge(Object obj, boolean immediately) {
    this.repo.merge(obj);
    if (immediately) {
      this.repo.flush();
    }
  }

  public void onAggregateLifecycle(
      @Observes(during = TransactionPhase.IN_PROGRESS) LifecycleEvent event) {
    if (event != null && event.getSource() != null) {
      if (event.getLifcyclehase() == LifcyclePhase.ENABLE) {
        if (event.getSource().getId() == null) {
          this.persist(event.getSource(), event.isEffectImmediately());
        } else {
          this.merge(event.getSource(), event.isEffectImmediately());
        }
      } else if (event.getLifcyclehase() == LifcyclePhase.DESTROY) {
        if (event.getSource().getId() != null) {
          this.remove(event.getSource(), event.isEffectImmediately());
        }
      }
    }
  }

  @Override
  public void persist(Object obj, boolean immediately) {
    this.repo.persist(obj);
    if (immediately) {
      this.repo.flush();
    }
  }

  @Override
  public void remove(Object obj, boolean immediately) {
    this.repo.remove(obj);
    if (immediately) {
      this.repo.flush();
    }
  }
}

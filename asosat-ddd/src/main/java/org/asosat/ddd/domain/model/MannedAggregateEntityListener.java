/*
 * Copyright (c) 2013-2018, Bingo.Chen (finesoft@gmail.com).
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
package org.asosat.ddd.domain.model;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.asosat.ddd.security.SecurityContextHolder;
import org.asosat.shared.Participator;

/**
 * corant-asosat-ddd
 *
 * @author bingo 下午1:27:26
 *
 */
public class MannedAggregateEntityListener {

  @SuppressWarnings("rawtypes")
  @PrePersist
  void onPrePersist(Object o) {
    if (o instanceof AbstractMannedAggregate) {
      AbstractMannedAggregate obj = AbstractMannedAggregate.class.cast(o);
      Participator participator = SecurityContextHolder.currentUser();
      if (obj.getCreator() == null) {
        obj.initCreationInfo(participator);
      }
      obj.initModificationInfo(participator);
    }
  }

  @PreUpdate
  void onPreUpdate(Object o) {
    if (o instanceof AbstractMannedAggregate) {
      AbstractMannedAggregate.class.cast(o)
          .initModificationInfo(SecurityContextHolder.currentUser());
    }
  }
}

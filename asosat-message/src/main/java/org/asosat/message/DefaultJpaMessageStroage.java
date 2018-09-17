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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.asosat.kernel.abstraction.Message;
import org.asosat.kernel.abstraction.MessageService.MessageStroage;
import org.asosat.kernel.pattern.repository.JpaRepository;
import org.asosat.kernel.util.JpaUtils;

/**
 * asosat-message
 *
 * @author bingo 下午10:24:18
 *
 */
@ApplicationScoped
public class DefaultJpaMessageStroage implements MessageStroage {

  @Inject
  JpaRepository repo;

  protected final Map<Class<?>, Boolean> persistMessageClasses =
      new ConcurrentHashMap<>(256, 0.75f, 256);

  @Transactional
  @Override
  public void store(Message message) {
    if (this.persistMessageClasses.computeIfAbsent(message.getClass(),
        JpaUtils::isPersistenceEntityClass)) {
      this.repo.persist(message);
      this.repo.flush();
    }
  }

}

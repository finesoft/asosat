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
package org.asosat.kernel.abstraction;

import java.io.Serializable;
import javax.persistence.EntityManager;

/**
 * @author bingo 上午12:26:44
 *
 */
public interface Entity extends Serializable {

  /**
   * @return
   */
  Serializable getId();

  public static interface EntityIdentifier extends Value, Readable<EntityIdentifier> {

    Serializable getId();

    Serializable getType();

  }

  @FunctionalInterface
  public static interface EntityManagerProvider {

    EntityManager getEntityManager();

  }

}

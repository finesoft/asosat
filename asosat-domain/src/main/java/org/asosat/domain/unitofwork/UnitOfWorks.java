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
package org.asosat.domain.unitofwork;

/**
 * @see <a href="http://martinfowler.com/eaaCatalog/unitOfWork.html">Unit Of Work</a>
 *
 * @author bingo 2013年5月8日
 */
public interface UnitOfWorks {

  public static final UnitOfWorks DUMMY_INST = new DummyUnitOfWorks();

  /**
   * Complete unit of works, clean registration, release effect.
   */
  void complete(boolean success);

  /**
   * The unit of works id
   *
   * @return
   */
  Object getId();

  /**
   * The registration in this unit of works
   */
  Object getRegistration();

  /**
   * Register an object to this unit of works
   *
   * @param obj
   */
  void register(Object obj);

  /**
   * Unregister an object from this unit of works
   * 
   * @param obj
   */
  default void unregister(Object obj) {

  }

  public static class DummyUnitOfWorks implements UnitOfWorks {

    @Override
    public void complete(boolean success) {}

    @Override
    public Object getId() {
      return null;
    }

    @Override
    public Object getRegistration() {
      return null;
    }

    @Override
    public void register(Object obj) {}

  }
}

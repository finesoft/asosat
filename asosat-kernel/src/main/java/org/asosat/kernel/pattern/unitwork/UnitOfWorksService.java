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

import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import org.asosat.kernel.abstraction.MessageService;
import org.asosat.kernel.annotation.stereotype.InfrastructureServices;
import org.asosat.kernel.pattern.saga.SagaService;

/**
 * asosat-domain <br/>
 *
 * @author bingo 上午11:51:01
 */
@ApplicationScoped
@InfrastructureServices
public interface UnitOfWorksService {

  UnitOfWorks getCurrentUnitOfWorks();

  Stream<UnitOfWorksHandler> getHandlers();

  Stream<UnitOfWorksListener> getListeners();

  MessageService getMessageService();

  SagaService getSagaService();

  @FunctionalInterface
  @ApplicationScoped
  @InfrastructureServices
  public static interface UnitOfWorksHandler {
    void onPreComplete(UnitOfWorks uow);
  }

  @FunctionalInterface
  @ApplicationScoped
  @InfrastructureServices
  public static interface UnitOfWorksListener {
    void onCompleted(Object registration, boolean success);
  }

}

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

import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import org.asosat.domain.annotation.stereotype.InfrastructureServices;
import org.asosat.domain.service.MessageService;

/**
 * asosat-domain <br/>
 *
 * @author bingo 上午11:51:01
 */
@ApplicationScoped
@InfrastructureServices
public interface UnitOfWorksManager {

  UnitOfWorks getCurrentUnitOfWorks();

  Stream<UnitOfWorksHandler> getHandlers();

  Stream<UnitOfWorksListener> getListeners();

  MessageService getMessageService();
}

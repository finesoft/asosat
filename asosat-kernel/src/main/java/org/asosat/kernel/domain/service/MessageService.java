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
package org.asosat.kernel.domain.service;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import org.asosat.kernel.domain.annotation.stereotype.InfrastructureServices;
import org.asosat.kernel.domain.message.ExchangedMessage;
import org.asosat.kernel.domain.message.MessageConvertor;
import org.asosat.kernel.domain.saga.SagaManager;
import org.asosat.kernel.pattern.interceptor.Asynchronous;

/**
 * @author bingo 上午10:48:06
 *
 */
@ApplicationScoped
@InfrastructureServices
public interface MessageService {

  MessageConvertor getConvertor();

  Stream<SagaManager> getSagaManagers(Annotation... annotations);

  @Asynchronous(fair = false)
  void receive(ExchangedMessage message);

  @Asynchronous(fair = false)
  void send(ExchangedMessage messages);

}

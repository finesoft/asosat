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
package org.asosat.ddd.service;

import java.io.Serializable;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import org.corant.suites.ddd.message.Message;
import org.corant.suites.ddd.message.MessageDispatcher;
import org.corant.suites.jms.shared.send.AnnotatedMessageSender;

/**
 * corant-suites-ddd
 *
 * @author bingo 下午3:34:06
 *
 */
@ApplicationScoped
@Transactional
public class DefaultJMSMessageDispatcher extends AnnotatedMessageSender
    implements MessageDispatcher {

  @Override
  public void accept(Message[] t) {
    super.send((Serializable[]) t);
  }

}

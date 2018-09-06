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
package org.asosat.thorntail.example.providers;

import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import org.asosat.domains.annotation.stereotype.InfrastructureServices;
import org.asosat.domains.message.AbstractGenericMessage;
import org.asosat.domains.message.AbstractGenericMessageConvertor;
import org.asosat.domains.message.ExchangedMessage;
import org.asosat.kernel.abstraction.Message;

/**
 * @author bingo 下午3:27:05
 *
 */
@ApplicationScoped
@InfrastructureServices
public class FakeMessageConvertor
    extends AbstractGenericMessageConvertor<Map<String, Object>, Map<String, Object>> {

  public FakeMessageConvertor() {}


  @Override
  public AbstractGenericMessage<Map<String, Object>, Map<String, Object>> from(
      ExchangedMessage message) {
    return super.from(message);
  }


  @Override
  public ExchangedMessage to(Message message) {
    return null;
  }

}

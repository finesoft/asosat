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

import org.asosat.kernel.abstraction.Message;
import org.asosat.kernel.stereotype.InfrastructureServices;

/**
 * @author bingo 上午10:13:53
 *
 */
@InfrastructureServices
public interface MessageHandler {

  void handle(Message message);

}
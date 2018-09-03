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
package org.asosat.domain.saga;

import java.util.Collection;
import org.asosat.kernel.supertype.Entity;
import org.asosat.kernel.supertype.Message.MessageIdentifier;

/**
 * @author bingo 下午2:02:19
 *
 */
public interface Saga extends Entity {

  Collection<? extends SagaAttribute> getAttributes();

  Object getOriginal();

  Collection<? extends MessageIdentifier> getRelevantMessages();

  String getTrackingToken();

  MessageIdentifier getTriggerMessage();

  boolean isActived();

  Saga withTrackingToken(String trackingToken);
}

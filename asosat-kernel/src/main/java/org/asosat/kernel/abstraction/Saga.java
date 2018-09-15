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

import java.util.Collection;
import org.asosat.kernel.abstraction.DynamicAttributes.DynamicNamedAttribute;
import org.asosat.kernel.abstraction.Message.MessageIdentifier;

/**
 * asosat-kernel
 *
 * @author bingo 上午11:22:36
 *
 */
public interface Saga extends Entity {

  Collection<? extends DynamicNamedAttribute> getAttributes();

  Object getOriginal();

  Collection<? extends MessageIdentifier> getRelevantMessages();

  String getTrackingToken();

  MessageIdentifier getTriggerMessage();

  boolean isActived();

  Saga withTrackingToken(String trackingToken);
}
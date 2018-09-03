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
package org.asosat.domain.aggregate;

import java.lang.annotation.Annotation;
import java.util.List;
import org.asosat.kernel.supertype.Being;
import org.asosat.kernel.supertype.Entity;
import org.asosat.kernel.supertype.Event;
import org.asosat.kernel.supertype.Lifecycle;
import org.asosat.kernel.supertype.Message;
import org.asosat.kernel.supertype.Readable;

/**
 * @author bingo 下午4:23:02
 * @since
 */
public interface Aggregate extends Entity, Being, Readable<Aggregate> {

  /**
   * If flush is true then the integration event queue will be clear
   */
  List<Message> extractMessages(boolean flush);

  /**
   * Fire events.
   */
  void fire(Event event, Annotation... qualifiers);

  /**
   * In this case, it means whether it is persisted or not
   */
  default boolean isEnabled() {
    return getLifecycle() == Lifecycle.ENABED;
  }

  /**
   * The aggregate isn't persisted, or is destroyed, but still live in memory until the GC recycle.
   */
  Boolean isPhantom();

  /**
   * Raise messages
   */
  void raise(Message... messages);

}
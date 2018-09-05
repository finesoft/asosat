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
package org.asosat.kernel.context;

import java.lang.annotation.Annotation;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import org.asosat.kernel.annotation.AsynchronousEvent;
import org.asosat.kernel.pattern.command.Command;
import org.asosat.kernel.pattern.command.Commander;

/**
 * @author bingo 下午7:06:13
 *
 */
public class DefaultContext {

  static CDI<Object> CTX;
  static {
    synchronized (DefaultContext.class) {
      try {
        CTX = CDI.current();
      } catch (Exception e) {

      }
    }
  }

  public static <U> U bean(Class<U> subtype, Annotation... qualifiers) {
    Instance<U> inst = CTX.select(subtype, qualifiers);
    if (inst.isResolvable()) {
      return inst.get();
    } else {
      return null;
    }
  }

  public static Commander commander() {
    return bean(Commander.class);
  }

  public static CDI<Object> current() {
    return CTX;
  }

  public static Event<Object> event() {
    return CTX.getBeanManager().getEvent();
  }

  public static void fireEvent(org.asosat.kernel.abstraction.Event event, Annotation... qualifiers) {
    if (event != null) {
      if (event.getClass().isAnnotationPresent(AsynchronousEvent.class)) {
        if (qualifiers.length > 0) {
          event().select(qualifiers).fireAsync(event);
        } else {
          event().fireAsync(event);
        }
      } else {
        if (qualifiers.length > 0) {
          event().select(qualifiers).fire(event);
        } else {
          event().fire(event);
        }
      }
    }
  }

  public static <R, C extends Command> R issueCommand(C cmd) {
    return commander().issue(cmd);
  }
}

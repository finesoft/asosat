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
package org.asosat.kernel.domain.annotation.qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

/**
 * @author bingo 下午9:05:13
 *
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE, FIELD, METHOD, PARAMETER})
@Qualifier
public @interface MessageQueue {

  String value() default MessageQueues.DFLT;

  public static class MessageQueueLiteral extends AnnotationLiteral<MessageQueue>
      implements MessageQueue {

    private static final long serialVersionUID = -5552841006073177750L;

    private final String value;

    private MessageQueueLiteral(String value) {
      this.value = value;
    }

    public static MessageQueueLiteral[] from(String... values) {
      return Arrays.stream(values).map(MessageQueueLiteral::of).collect(Collectors.toList())
          .toArray(new MessageQueueLiteral[0]);
    }

    public static MessageQueueLiteral of(String value) {
      return new MessageQueueLiteral(value);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!super.equals(obj)) {
        return false;
      }
      if (this.getClass() != obj.getClass()) {
        return false;
      }
      MessageQueueLiteral other = (MessageQueueLiteral) obj;
      if (this.value == null) {
        if (other.value != null) {
          return false;
        }
      } else if (!this.value.equals(other.value)) {
        return false;
      }
      return true;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
      return result;
    }

    @Override
    public String value() {
      return this.value;
    }
  }
}

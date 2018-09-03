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
package org.asosat.kernel.annotation.jee;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.BeforeDestroyed;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.util.AnnotationLiteral;

/**
 * @author bingo 下午4:46:14
 *
 */
@SuppressWarnings("all")
public class BeforeDestroyedLiteral extends AnnotationLiteral<BeforeDestroyed>
    implements BeforeDestroyed {

  public static final BeforeDestroyedLiteral REQUEST = of(RequestScoped.class);

  public static final BeforeDestroyedLiteral CONVERSATION = of(ConversationScoped.class);

  public static final BeforeDestroyedLiteral SESSION = of(SessionScoped.class);

  public static final BeforeDestroyedLiteral APPLICATION = of(ApplicationScoped.class);

  private static final long serialVersionUID = 1L;

  private final Class<? extends Annotation> value;

  private BeforeDestroyedLiteral(Class<? extends Annotation> value) {
    this.value = value;
  }

  public static BeforeDestroyedLiteral of(Class<? extends Annotation> value) {
    return new BeforeDestroyedLiteral(value);
  }

  @Override
  public Class<? extends Annotation> value() {
    return this.value;
  }

}

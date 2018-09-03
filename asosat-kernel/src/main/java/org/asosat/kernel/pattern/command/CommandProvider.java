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
package org.asosat.kernel.pattern.command;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Provider;

/**
 * @author bingo 下午2:04:50
 *
 */
public class CommandProvider<H extends CommandHandler<?, ?>> implements Provider<H> {

  private final BeanManager beanManager;
  @SuppressWarnings("rawtypes")
  private final Class<? extends CommandHandler> handlerClass;

  CommandProvider(BeanManager beanManager, Class<? extends CommandHandler<?, ?>> handlerClass) {
    this.beanManager = beanManager;
    this.handlerClass = handlerClass;
  }

  @SuppressWarnings("unchecked")
  @Override
  public H get() {
    Bean<H> handlerBean = (Bean<H>) this.beanManager.getBeans(this.handlerClass).iterator().next();
    CreationalContext<H> context = this.beanManager.createCreationalContext(handlerBean);
    return (H) this.beanManager.getReference(handlerBean, this.handlerClass, context);
  }
}


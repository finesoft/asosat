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

import static org.asosat.kernel.util.Preconditions.requireNull;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import org.asosat.kernel.resource.GlobalMessageCodes;

/**
 * @author bingo 下午2:36:49
 *
 */
@SuppressWarnings("unchecked")
public class CommandExtension implements Extension {

  private Map<Class<? extends Command>, Class<? extends CommandHandler<?, ?>>> commandHandlers =
      new HashMap<>();

  public CommandExtension() {}


  public <H extends CommandHandler<?, ?>> void captureCommandHandlers(
      @Observes ProcessInjectionTarget<H> target) {
    Class<H> handler = target.getAnnotatedType().getJavaClass();
    for (Type type : target.getAnnotatedType().getTypeClosure()) {
      if (type instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type genericParameterType = parameterizedType.getActualTypeArguments()[1];
        if (genericParameterType instanceof Class
            && Command.class.isAssignableFrom((Class<?>) genericParameterType)) {
          requireNull(
              this.commandHandlers.put((Class<? extends Command>) genericParameterType, handler),
              GlobalMessageCodes.ERR_SYS);
        }
      }
    }
  }

  @SuppressWarnings("rawtypes")
  public void register(@Observes AfterDeploymentValidation event, final BeanManager beanManager) {
    CommandRegistry registry = this.getRegistry(beanManager);
    this.commandHandlers.forEach((commandClass, handlerClass) -> {
      registry.register(commandClass, new CommandProvider(beanManager, handlerClass));
    });
  }

  private CommandRegistry getRegistry(BeanManager beanManager) {
    Bean<CommandRegistry> registryBean =
        (Bean<CommandRegistry>) beanManager.getBeans(CommandRegistry.class).iterator().next();
    CreationalContext<CommandRegistry> context = beanManager.createCreationalContext(registryBean);
    return (CommandRegistry) beanManager.getReference(registryBean, CommandRegistry.class, context);
  }

}

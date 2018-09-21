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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.commons.lang3.mutable.MutableInt;
import org.asosat.kernel.exception.KernelRuntimeException;

/**
 * @author bingo 下午2:07:00
 *
 */
@ApplicationScoped
public class DefaultCommander implements Commander {

  final CommandRegistry registry;

  final ThreadLocal<MutableInt> commandStacks = ThreadLocal.withInitial(MutableInt::new);

  @Inject
  public DefaultCommander(CommandRegistry registry) {
    this.registry = registry;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <R, C extends Command> R issue(C command) {
    try {
      this.commandStacks.get().increment();
      CommandHandler<C, R> commandHandler =
          (CommandHandler<C, R>) this.registry.get(command.getClass());
      return commandHandler.handle(command);
    } catch (Exception ex) {
      throw new KernelRuntimeException(ex);
    } finally {
      this.commandStacks.get().decrement();
      if (this.commandStacks.get().intValue() == 0) {
        CommandContext.clear();
      }
    }
  }

}

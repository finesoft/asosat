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

import java.util.ArrayList;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.asosat.kernel.exception.KernelRuntimeException;

/**
 * @author bingo 下午2:07:00
 *
 */
@ApplicationScoped
public class DefaultCommander implements Commander {

  final CommandRegistry registry;

  final ThreadLocal<ArrayList<Command>> commandStacks = ThreadLocal.withInitial(ArrayList::new);

  @Inject
  public DefaultCommander(CommandRegistry registry) {
    this.registry = registry;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <R, C extends Command> R issue(C command) {
    try {
      this.commandStacks.get().add(command);
      CommandHandler<C, R> commandHandler =
          (CommandHandler<C, R>) this.registry.get(command.getClass());
      return commandHandler.handle(command);
    } catch (Exception ex) {
      throw new KernelRuntimeException(ex);
    } finally {
      ArrayList<Command> stack = this.commandStacks.get();
      int size = stack.size();
      if (size > 0) {
        stack.remove(size - 1);
      }
      if (stack.isEmpty()) {
        CommandContext.clear();
      }
    }
  }

}

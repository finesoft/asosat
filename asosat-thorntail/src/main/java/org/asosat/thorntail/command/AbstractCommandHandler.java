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
package org.asosat.thorntail.command;

import java.util.Map;
import javax.inject.Qualifier;
import org.asosat.kernel.abstraction.Event;
import org.asosat.kernel.abstraction.Message;
import org.asosat.kernel.context.DefaultContext;
import org.asosat.kernel.pattern.command.Command;
import org.asosat.kernel.pattern.command.CommandHandler;
import org.asosat.kernel.pattern.unitwork.AbstractTxJpaUnitOfWorksxService;
import org.asosat.kernel.pattern.unitwork.DefaultTxJpaUnitOfWorks;
import org.asosat.kernel.stereotype.ApplicationServices;

/**
 * @author bingo 下午5:40:30
 *
 */
@ApplicationServices
public abstract class AbstractCommandHandler<C extends AbstractCommand, R>
    implements CommandHandler<C, R> {

  public AbstractCommandHandler() {}

  protected void fire(Event event, Qualifier... qualifiers) {
    DefaultContext.fireEvent(event, qualifiers);
  }

  protected void fireAsync(Event event, Qualifier... qualifiers) {
    DefaultContext.fireAsyncEvent(event, qualifiers);
  }

  protected <SR, SC extends Command> SR issue(SC cmd) {
    return DefaultContext.issueCommand(cmd);
  }

  protected void raise(Message... messages) {
    DefaultTxJpaUnitOfWorks uows = AbstractTxJpaUnitOfWorksxService.currentUnitOfWorks();
    if (uows != null) {
      for (Message msg : messages) {
        uows.register(msg);
      }
    }
  }

  protected CommandResultBuilder result() {
    return new CommandResultBuilder();
  }

  public static class CommandResultBuilder {

    final CommandResult result = new CommandResult();

    public CommandResult build() {
      return this.result.unmodifiable();
    }

    public CommandResultBuilder clear() {
      this.result.clear();
      return this;
    }

    public CommandResultBuilder with(Map<String, Object> map) {
      this.result.putAll(map);
      return this;
    }

    public CommandResultBuilder with(String key, Object value) {
      this.result.put(key, value);
      return this;
    }

    public CommandResultBuilder without(String key) {
      this.result.remove(key);
      return this;
    }
  }
}

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

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

/**
 * @author bingo 下午2:05:47
 *
 */
@ApplicationScoped
public class CommandRegistry {

  @SuppressWarnings("rawtypes")
  private Map<Class<? extends Command>, CommandProvider> providerMap = new HashMap<>();

  public CommandRegistry() {}

  @SuppressWarnings("unchecked")
  <R, C extends Command> CommandHandler<C, R> get(Class<C> commandClass) {
    return this.providerMap.get(commandClass).get();
  }

  @SuppressWarnings("rawtypes")
  void register(Class<? extends Command> commandClass, CommandProvider provider) {
    this.providerMap.put(commandClass, provider);
  }

}

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
package org.asosat.zest.throntail.command;

import static org.asosat.kernel.resource.GlobalMessageCodes.ERR_PARAM;
import static org.asosat.kernel.util.Preconditions.requireNotNull;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.asosat.kernel.pattern.command.Command;
import org.asosat.kernel.pattern.command.CommandHandler;
import org.asosat.zest.throntail.domain.Order;
import org.asosat.zest.throntail.domain.Repository;

/**
 * asosat-thorntail-example
 *
 * @author bingo 下午2:15:20
 *
 */
public class RemoveOrder {

  public static class RemoveOrderCmd implements Command {

    private long id;

    public long getId() {
      return this.id;
    }

    public RemoveOrderCmd id(long id) {
      this.id = id;
      return this;
    }
  }

  @Transactional
  public static class RemoveOrderHandler implements CommandHandler<RemoveOrderCmd, Void> {

    @Inject
    Repository repo;

    @Override
    public Void handle(RemoveOrderCmd command) {
      requireNotNull(this.repo.get(Order.class, command.getId()), ERR_PARAM).destroy(null,
          (p, o) -> System.out.println("We will remove the order!"));
      return null;
    }
  }
}

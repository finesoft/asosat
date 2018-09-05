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
package org.asosat.thorntail.example.command;

import static org.asosat.kernel.util.MyMapUtils.getMapValue;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.asosat.kernel.pattern.command.Command;
import org.asosat.kernel.pattern.command.CommandHandler;
import org.asosat.kernel.util.ConvertUtils;
import org.asosat.thorntail.example.domain.Order;

/**
 * asosat-thorntail-example
 *
 * @author bingo 下午12:12:43
 *
 */
public class SaveOrder {

  public static class SaveOrderCmd implements Command {

    private String buyer;

    private String seller;

    private String number;

    private List<Map<String, Object>> items = new LinkedList<>();

    public SaveOrderCmd buyer(String buyer) {
      this.buyer = buyer;
      return this;
    }

    public String getBuyer() {
      return this.buyer;
    }

    public List<Map<String, Object>> getItems() {
      return this.items;
    }

    public String getNumber() {
      return this.number;
    }

    public String getSeller() {
      return this.seller;
    }

    public SaveOrderCmd items(List<Map<String, Object>> items) {
      this.items = items;
      return this;
    }

    public SaveOrderCmd number(String number) {
      this.number = number;
      return this;
    }

    public SaveOrderCmd seller(String seller) {
      this.seller = seller;
      return this;
    }
  }

  @Transactional
  public static class SaveOrderCmdHandler implements CommandHandler<SaveOrderCmd, Long> {

    @Override
    public Long handle(SaveOrderCmd command) {
      Order order = new Order(command.getNumber(), command.getSeller(), command.getBuyer());
      command.getItems().forEach(i -> {
        order.addItem(getMapValue(i, "product", ConvertUtils::toString),
            getMapValue(i, "qty", ConvertUtils::toBigDecimal),
            getMapValue(i, "unitPrice", ConvertUtils::toBigDecimal));
      });
      return order.enable(null, (p, o) -> System.out.println("We will save the order!")).getId();
    }

  }
}

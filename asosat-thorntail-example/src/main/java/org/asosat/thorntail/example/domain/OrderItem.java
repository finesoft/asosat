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
package org.asosat.thorntail.example.domain;

import static org.asosat.kernel.util.Preconditions.requireGt;
import static org.asosat.kernel.util.Preconditions.requireNotBlank;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * asosat-thorntail-example
 *
 * @author bingo 下午7:33:06
 *
 */
@Embeddable
public class OrderItem {

  @Column
  private String product;

  @Column
  private BigDecimal qty;

  @Column
  private BigDecimal unitPrice;

  /**
   * @param product
   * @param qty
   * @param unitPrice
   */
  public OrderItem(String product, BigDecimal qty, BigDecimal unitPrice) {
    super();
    this.product = requireNotBlank(product, "");
    this.qty = requireGt(qty, BigDecimal.ZERO, "");
    this.unitPrice = requireGt(unitPrice, BigDecimal.ZERO, "");
  }

  protected OrderItem() {}



  /**
   *
   * @return the product
   */
  public String getProduct() {
    return this.product;
  }

  /**
   *
   * @return the qty
   */
  public BigDecimal getQty() {
    return this.qty;
  }

  /**
   *
   * @return the unitPrice
   */
  public BigDecimal getUnitPrice() {
    return this.unitPrice;
  }

  /**
   *
   * @param qty the qty to set
   */
  protected void updateQty(BigDecimal qty) {
    this.qty = requireGt(qty, BigDecimal.ZERO, "");
  }

  /**
   *
   * @param unitPrice the unitPrice to set
   */
  protected void updateUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = requireGt(unitPrice, BigDecimal.ZERO, "");
  }



}

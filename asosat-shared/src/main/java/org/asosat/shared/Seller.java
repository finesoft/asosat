/*
 * Copyright (c) 2013-2018, Bingo.Chen (finesoft@gmail.com).
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
package org.asosat.shared;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;

@Embeddable
@MappedSuperclass
@AttributeOverrides(value = {@AttributeOverride(column = @Column(name = "sellerId"), name = "id"),
    @AttributeOverride(column = @Column(name = "sellerName"), name = "name")})
public class Seller extends Participator {

  private static final long serialVersionUID = 2482047799269041296L;

  public static Seller of(Participator participator) {
    return new Seller(participator.getId(), participator.getName());
  }

  protected Seller() {
    super();
  }

  public Seller(Long id, String name) {
    super(id, name);
  }

  @Override
  protected void setId(Long id) {
    super.setId(id);
  }

  @Override
  protected void setName(String name) {
    super.setName(name);
  }
}

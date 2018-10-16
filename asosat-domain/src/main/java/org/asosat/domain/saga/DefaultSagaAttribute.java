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
package org.asosat.domain.saga;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;
import org.asosat.kernel.abstraction.DynamicAttributes.AttributeType;
import org.asosat.kernel.normal.conversion.Conversions;
import org.asosat.kernel.abstraction.Value;

/**
 * @author bingo 下午5:59:30
 *
 */
@MappedSuperclass
@Embeddable
public class DefaultSagaAttribute implements SagaAttribute, Value {

  private static final long serialVersionUID = -8719994616061589039L;

  @Column
  private String name;

  @Column
  private AttributeType type;

  @Column
  private Boolean boolValue;

  @Column
  private BigDecimal numberValue;

  @Column
  private String stringValue;

  @Column
  private ZonedDateTime temporalValue;

  public DefaultSagaAttribute(String name, Boolean boolValue) {
    this.setType(AttributeType.BOOLEAN);
    this.setName(name);
    this.setBoolValue(Conversions.toBoolean(boolValue));
  }

  public DefaultSagaAttribute(String name, Number numberValue) {
    this.setType(AttributeType.NUMBERIC);
    this.setName(name);
    this.setNumberValue(Conversions.toBigDecimal(numberValue));
  }

  public DefaultSagaAttribute(String name, String stringValue) {
    this.setType(AttributeType.STRING);
    this.setName(name);
    this.setStringValue(stringValue);
  }

  public DefaultSagaAttribute(String name, Temporal temporalValue) {
    this.setType(AttributeType.TEMPORAL);
    this.setName(name);
    this.setTemporalValue(Conversions.toZonedDateTime(temporalValue));
  }

  protected DefaultSagaAttribute() {}

  @Override
  public Boolean getBoolValue() {
    return this.boolValue;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public BigDecimal getNumberValue() {
    return this.numberValue;
  }

  @Override
  public String getStringValue() {
    return this.stringValue;
  }

  @Override
  public ZonedDateTime getTemporalValue() {
    return this.temporalValue;
  }

  @Override
  public AttributeType getType() {
    return this.type;
  }

  protected void setBoolValue(Boolean boolValue) {
    this.boolValue = boolValue;
  }

  protected void setName(String name) {
    this.name = name;
  }

  protected void setNumberValue(BigDecimal numberValue) {
    this.numberValue = numberValue;
  }

  protected void setStringValue(String stringValue) {
    this.stringValue = stringValue;
  }

  protected void setTemporalValue(ZonedDateTime temporalValue) {
    this.temporalValue = temporalValue;
  }

  protected void setType(AttributeType type) {
    this.type = type;
  }

}

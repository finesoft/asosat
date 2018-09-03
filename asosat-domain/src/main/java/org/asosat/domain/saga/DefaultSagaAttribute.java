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
import org.asosat.kernel.supertype.DynamicAttributes.DynamicAttributeType;
import org.asosat.kernel.util.ConvertUtils;
import org.asosat.domain.aggregate.AbstractValueObject;

/**
 * @author bingo 下午5:59:30
 *
 */
@MappedSuperclass
@Embeddable
public class DefaultSagaAttribute extends AbstractValueObject implements SagaAttribute {

  private static final long serialVersionUID = -8719994616061589039L;

  @Column
  private String name;

  @Column
  private DynamicAttributeType type;

  @Column
  private Boolean boolValue;

  @Column
  private BigDecimal numberValue;

  @Column
  private String stringValue;

  @Column
  private ZonedDateTime temporalValue;

  public DefaultSagaAttribute(String name, Boolean boolValue) {
    this.setType(DynamicAttributeType.BOOL);
    this.setName(name);
    this.setBoolValue(ConvertUtils.toBoolean(boolValue));
  }

  public DefaultSagaAttribute(String name, Number numberValue) {
    this.setType(DynamicAttributeType.NUMBER);
    this.setName(name);
    this.setNumberValue(ConvertUtils.toBigDecimal(numberValue));
  }

  public DefaultSagaAttribute(String name, String stringValue) {
    this.setType(DynamicAttributeType.STRING);
    this.setName(name);
    this.setStringValue(stringValue);
  }

  public DefaultSagaAttribute(String name, Temporal temporalValue) {
    this.setType(DynamicAttributeType.TEMPORAL);
    this.setName(name);
    this.setTemporalValue(ConvertUtils.toZonedDateTime(temporalValue));
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
  public DynamicAttributeType getType() {
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

  protected void setType(DynamicAttributeType type) {
    this.type = type;
  }

}

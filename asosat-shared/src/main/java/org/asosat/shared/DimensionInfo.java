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

import static org.corant.shared.util.Assertions.shouldBeTrue;
import static org.corant.shared.util.Assertions.shouldNotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * corant-asosat-ddd
 *
 * @author bingo 下午2:51:50
 */
@MappedSuperclass
@Embeddable
public class DimensionInfo implements ValueObject {

  private static final long serialVersionUID = -2411775138449357236L;

  @Column
  private BigDecimal height;

  @Column
  private BigDecimal length;

  @Column
  private BigDecimal width;

  @Column(length = 8)
  @Enumerated(EnumType.STRING)
  private MeasureUnit unit;

  public DimensionInfo(BigDecimal length, BigDecimal width, BigDecimal height, MeasureUnit unit) {
    this(length, width, height, unit, false);
  }

  public DimensionInfo(BigDecimal length, BigDecimal width, BigDecimal height, MeasureUnit unit,
      boolean strict) {
    super();
    if (strict) {
      shouldNotNull(length, "DimensionInfo.len_error_null");
      shouldNotNull(width, "DimensionInfo.width_error_null");
      shouldNotNull(height, "DimensionInfo.height_error_null");
      shouldNotNull(unit, "DimensionInfo.sizeUnit_error_null");
    }
    setLength(length);
    setWidth(width);
    setHeight(height);
    setUnit(unit);
  }

  public DimensionInfo(DimensionInfo other) {
    this(other.getHeight(), other.getWidth(), other.getHeight(), other.getUnit());
  }

  protected DimensionInfo() {

  }

  public String asDescription() {
    return asDescription(0, "%s x %s x %s %s");
  }

  public String asDescription(int fixScala, String format) {
    return String.format(format,
        length == null ? "?"
            : fixScala < 0 ? length
                : length.setScale(fixScala, RoundingMode.HALF_UP).toPlainString(),
        width == null ? "?"
            : fixScala < 0 ? width : width.setScale(fixScala, RoundingMode.HALF_UP).toPlainString(),
        height == null ? "?"
            : fixScala < 0 ? height
                : height.setScale(fixScala, RoundingMode.HALF_UP).toPlainString(),
        unit == null ? "" : unit.toString());
  }

  /**
   * @param unit
   * @return
   */
  public BigDecimal calVolume(MeasureUnit unit) {
    return calVolume(unit, null, null, null);
  }

  /**
   * @param unit
   * @param lengthOffset
   * @param widthOffset
   * @param heightOffset
   * @return
   */
  public BigDecimal calVolume(MeasureUnit unit, BigDecimal lengthOffset, BigDecimal widthOffset,
      BigDecimal heightOffset) {
    if (isConsistency()) {
      BigDecimal length = getLength().add(lengthOffset == null ? BigDecimal.ZERO : lengthOffset);
      BigDecimal width = getWidth().add(widthOffset == null ? BigDecimal.ZERO : widthOffset);
      BigDecimal height = getHeight().add(heightOffset == null ? BigDecimal.ZERO : heightOffset);
      if (unit == null || unit == getUnit()) {
        return length.multiply(width).multiply(height);
      } else {
        return getUnit().convert(length, unit).multiply(getUnit().convert(width, unit))
            .multiply(getUnit().convert(height, unit));
      }
    }
    return null;
  }

  public boolean canFit(DimensionInfo other) {
    return canFit(other, null, null, null, null);
  }

  public boolean canFit(DimensionInfo other, BigDecimal lengthOffset, BigDecimal widthOffset,
      BigDecimal heightOffset, MeasureUnit offsetUnit) {
    if (!isConsistency() || other == null || !other.isConsistency()) {
      return false;
    }
    MeasureUnit ofsUn = offsetUnit == null ? getUnit() : offsetUnit;
    BigDecimal lenOfs = lengthOffset == null ? BigDecimal.ZERO : lengthOffset;
    BigDecimal widOfs = lengthOffset == null ? BigDecimal.ZERO : widthOffset;
    BigDecimal higOfs = lengthOffset == null ? BigDecimal.ZERO : heightOffset;
    return getUnit().convert(getLength()).add(ofsUn.convert(lenOfs))
        .compareTo(other.getUnit().convert(other.getLength())) >= 0
        && getUnit().convert(getWidth()).add(ofsUn.convert(widOfs))
            .compareTo(other.getUnit().convert(other.getWidth())) >= 0
        && getUnit().convert(getHeight()).add(ofsUn.convert(higOfs))
            .compareTo(other.getUnit().convert(other.getHeight())) >= 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    DimensionInfo other = (DimensionInfo) obj;
    if (getHeight() == null) {
      if (other.getHeight() != null) {
        return false;
      }
    } else if (!getHeight().equals(other.getHeight())) {
      return false;
    }
    if (getLength() == null) {
      if (other.getLength() != null) {
        return false;
      }
    } else if (!getLength().equals(other.getLength())) {
      return false;
    }
    if (getUnit() != other.getUnit()) {
      return false;
    }
    if (getWidth() == null) {
      if (other.getWidth() != null) {
        return false;
      }
    } else if (!getWidth().equals(other.getWidth())) {
      return false;
    }
    return true;
  }

  public BigDecimal getHeight() {
    return height;
  }

  public BigDecimal getLength() {
    return length;
  }

  public MeasureUnit getUnit() {
    return unit;
  }

  public BigDecimal getWidth() {
    return width;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (getHeight() == null ? 0 : getHeight().hashCode());
    result = prime * result + (getLength() == null ? 0 : getLength().hashCode());
    result = prime * result + (getUnit() == null ? 0 : getUnit().hashCode());
    result = prime * result + (getWidth() == null ? 0 : getWidth().hashCode());
    return result;
  }

  @Transient
  public boolean isConsistency() {
    return getLength() != null && getWidth() != null && getHeight() != null && getUnit() != null;
  }

  protected BigDecimal scaleValue(BigDecimal value) {
    return Measurables.defaultScale(value);
  }

  protected void setHeight(BigDecimal height) {
    if (height != null) {
      BigDecimal value = scaleValue(height);
      shouldBeTrue(value.compareTo(BigDecimal.ZERO) > 0, "DimensionInfo.len_error_null");
      this.height = value;
    } else {
      this.height = null;
    }
  }

  protected void setLength(BigDecimal length) {
    if (length != null) {
      BigDecimal value = scaleValue(length);
      shouldBeTrue(value.compareTo(BigDecimal.ZERO) > 0, "DimensionInfo.len_error_null");
      this.length = value;
    } else {
      this.length = null;
    }
  }

  protected void setUnit(MeasureUnit unit) {
    if (unit != null) {
      shouldBeTrue(unit.isLength(), "DimensionInfo.sizeUnit_error_null");
    }
    this.unit = unit;
  }

  protected void setWidth(BigDecimal width) {
    if (width != null) {
      BigDecimal value = scaleValue(width);
      shouldBeTrue(value.compareTo(BigDecimal.ZERO) > 0, "DimensionInfo.len_error_null");
      this.width = value;
    } else {
      this.width = null;
    }
  }
}

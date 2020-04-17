package org.asosat.ddd.exchange;

import static org.apache.poi.ss.usermodel.IndexedColors.PALE_BLUE;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * @author don
 * @date 2020-01-03
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {

  String INSTANT_FORMAT = "yyyy-mm-dd hh:mm";
  String DATE_FORMAT = "yyyy-mm-dd";

  String PERCENT_FORMAT = "0%";
  String PERCENT_00_FORMAT = "0.00%";

  String THOUSAND_SEPARATOR_FORMAT = "#,##0";
  String THOUSAND_SEPARATOR_00_FORMAT = "#,##0.00";

  Integer DEFAULT_COLUMN_WIDTH = 2046;

  IndexedColors DEFAULT_COLOR = PALE_BLUE;

  /** 用于导入 */
  boolean extract() default false;

  /** 对应列字母索引 */
  String alphabet();

  /** 列标题 */
  String title();

  /** 格式 */
  String dataFormat() default "";

  /** 枚举文字 */
  boolean enumLiteral() default true;

  /**
   * 列宽
   */
  int width() default 2046;

  IndexedColors color() default PALE_BLUE;
}
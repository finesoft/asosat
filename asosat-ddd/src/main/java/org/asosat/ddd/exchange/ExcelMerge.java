package org.asosat.ddd.exchange;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author don
 * @date 2020-01-31
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelMerge {

  enum MergeIndicate {
    START,
    END
  }

  MergeIndicate value();

  String title() default "";
}

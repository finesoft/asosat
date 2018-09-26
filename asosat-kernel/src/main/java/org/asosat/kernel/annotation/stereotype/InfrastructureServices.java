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
package org.asosat.kernel.annotation.stereotype;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.util.AnnotationLiteral;

/**
 * @author bingo 下午6:30:49
 *
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE, FIELD, METHOD})
@Inherited
@Stereotype
public @interface InfrastructureServices {

  final static InfrastructureServicesLiteral INST = new InfrastructureServicesLiteral();

  public static class InfrastructureServicesLiteral
      extends AnnotationLiteral<InfrastructureServices> {

    private static final long serialVersionUID = -2729159220699119193L;
  }
}
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
package org.asosat.kernel.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import org.apache.commons.lang3.AnnotationUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

/**
 * @author bingo 下午10:06:13
 *
 */
public class MyAnnUtils extends AnnotationUtils {

  @SuppressWarnings("unchecked")
  public static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement,
      Class<A> annotationType) {
    Annotation[] anns = annotatedElement.getDeclaredAnnotations();
    for (Annotation ann : anns) {
      if (ann.annotationType() == annotationType) {
        return (A) ann;
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType,
      boolean searchSupers) {
    Annotation[] anns = clazz.getDeclaredAnnotations();
    for (Annotation ann : anns) {
      if (ann.annotationType() == annotationType) {
        return (A) ann;
      }
    }
    if (searchSupers) {
      Class<?> superclass = clazz.getSuperclass();
      if (superclass == null || Object.class == superclass) {
        return null;
      }
      return findAnnotation(superclass, annotationType);
    }
    return null;
  }

  public static <A extends Annotation> A findAnnotation(Method method, Class<A> annotationType,
      boolean searchSupers, boolean ignoreAccess) {
    return MethodUtils.getAnnotation(method, annotationType, searchSupers, ignoreAccess);
  }
}

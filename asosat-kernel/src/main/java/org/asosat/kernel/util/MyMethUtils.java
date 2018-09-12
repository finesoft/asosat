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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.enterprise.inject.spi.AnnotatedMethod;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

/**
 * @author bingo 下午10:08:22
 *
 */
public class MyMethUtils extends MethodUtils {

  public static void loopMethods(Class<?> clazz, Consumer<Method> mc) {
    loopMethods(clazz, mc, null);
  }

  public static void loopMethods(Class<?> clazz, Consumer<Method> mc, Predicate<Method> mf) {
    Method[] methods = clazz.getMethods();
    for (Method method : methods) {
      if (mf != null && !mf.test(method)) {
        continue;
      }
      try {
        mc.accept(method);
      } catch (Exception ex) {
        throw new IllegalStateException(
            "Not allowed to access method '" + method.getName() + "': " + ex);
      }
    }
    if (clazz.getSuperclass() != null) {
      loopMethods(clazz.getSuperclass(), mc, mf);
    } else if (clazz.isInterface()) {
      for (Class<?> superIfc : clazz.getInterfaces()) {
        loopMethods(superIfc, mc, mf);
      }
    }
  }

  public static class MethodSignature implements Serializable {

    private static final long serialVersionUID = 2424253135982857193L;

    private final String methodName;

    private final String[] parameterTypes;

    public MethodSignature(AnnotatedMethod<?> method) {
      this.methodName = method.getJavaMember().getName();
      this.parameterTypes = new String[method.getParameters().size()];
      for (int i = 0; i < method.getParameters().size(); i++) {
        this.parameterTypes[i] =
            TypeUtils.getRawType(method.getParameters().get(i).getBaseType(), null).getName();
      }
    }

    public MethodSignature(Method method) {
      this.methodName = method.getName();
      this.parameterTypes = new String[method.getParameterTypes().length];
      for (int i = 0; i < method.getParameterTypes().length; i++) {
        this.parameterTypes[i] = method.getParameterTypes()[i].getName();
      }
    }

    public MethodSignature(String methodName, String... parameterTypes) {
      this.methodName = methodName;
      this.parameterTypes = parameterTypes;
    }

    public static MethodSignature of(AnnotatedMethod<?> method) {
      return new MethodSignature(method);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (!(obj instanceof MethodSignature)) {
        return false;
      }
      MethodSignature other = (MethodSignature) obj;
      if (this.methodName == null) {
        if (other.methodName != null) {
          return false;
        }
      } else if (!this.methodName.equals(other.methodName)) {
        return false;
      }
      return Arrays.equals(this.parameterTypes, other.parameterTypes);
    }

    public String getMethodName() {
      return this.methodName;
    }

    public String[] getParameterTypes() {
      return Arrays.copyOf(this.parameterTypes, this.parameterTypes.length);
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + this.methodName.hashCode();
      result = prime * result + Arrays.hashCode(this.parameterTypes);
      return result;
    }

    public boolean matches(Method method) {
      if (!this.methodName.equals(method.getName())) {
        return false;
      }
      final Class<?>[] methodParameterTypes = method.getParameterTypes();
      if (methodParameterTypes.length != this.parameterTypes.length) {
        return false;
      }
      for (int i = 0; i < this.parameterTypes.length; i++) {
        if (!this.parameterTypes[i].equals(methodParameterTypes[i].getName())) {
          return false;
        }
      }
      return true;
    }

    @Override
    public String toString() {
      return new StringBuffer().append("method ").append(this.getMethodName())
          .append(Arrays.toString(this.parameterTypes).replace('[', '(').replace(']', ')'))
          .toString();
    }

  }
}

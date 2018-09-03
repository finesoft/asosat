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

import static org.asosat.kernel.util.Preconditions.requireNotBlank;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ClassUtils.Interfaces;
import org.apache.commons.vfs2.FileExtensionSelector;
import org.asosat.kernel.resource.GlobalMessageCodes;
import org.asosat.kernel.resource.MultiClassPathFiles;

/**
 * @author bingo 下午10:31:37
 *
 */
public class MyClsUtils {

  public static final String CGLIB_CLASS_SEPARATOR = "$$";

  public static ClassLoader defaultClassLoader() {
    ClassLoader cl = null;
    try {
      cl = Thread.currentThread().getContextClassLoader();
    } catch (Throwable ex) {
    }
    if (cl == null) {
      cl = MyClsUtils.class.getClassLoader();
      if (cl == null) {
        try {
          cl = ClassLoader.getSystemClassLoader();
        } catch (Throwable ex) {
        }
      }
    }
    return cl;
  }

  public static Class<?> forName(final ClassLoader classLoader, final String className)
      throws ClassNotFoundException {
    return ClassUtils.getClass(classLoader, className);
  }

  public static Class<?> forName(final ClassLoader classLoader, final String className,
      final boolean initialize) throws ClassNotFoundException {
    return ClassUtils.getClass(classLoader, className, initialize);
  }

  public static Class<?> forName(final String className) throws ClassNotFoundException {
    return forName(className, true);
  }

  public static Class<?> forName(final String className, final boolean initialize)
      throws ClassNotFoundException {
    return ClassUtils.getClass(className, initialize);
  }

  public static List<Class<?>> getAllInterfaces(final Object inst) {
    if (inst == null || inst.getClass() == Object.class) {
      return null;
    }
    return getAllInterfaces(inst.getClass());
  }

  public static List<Class<?>> getAllSuperclasses(final Object inst) {
    if (inst == null || inst.getClass() == Object.class) {
      return null;
    } else {
      return getAllSuperclasses(inst.getClass());
    }
  }

  public static List<String> getClassPathPackageClassNames(String packageName) {
    List<String> classNames = new ArrayList<>();
    String packageNameToUse =
        requireNotBlank(packageName, GlobalMessageCodes.ERR_PARAM).replaceAll("\\.", "/");
    MultiClassPathFiles.select(new FileExtensionSelector("class")).forEach((s, fo) -> {
      if (s.contains(packageNameToUse)) {
        String className = (s.substring(s.indexOf(packageNameToUse), s.lastIndexOf(".class")))
            .replaceAll("/", ".");
        if (!classNames.contains(className)) {
          classNames.add(className);
        }
      }
    });
    classNames.sort(String::compareTo);
    return classNames;
  }

  public static Class<?> getUserClass(Class<?> clazz) {
    if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
      Class<?> superclass = clazz.getSuperclass();
      if (superclass != null && Object.class != superclass) {
        return superclass;
      }
    }
    return clazz;
  }

  public static Class<?> getUserClass(Object instance) {
    return getUserClass(instance.getClass());
  }

  public static Iterable<Class<?>> hierarchy(final Class<?> type, boolean includeInterfaces) {
    return ClassUtils.hierarchy(type, includeInterfaces ? Interfaces.INCLUDE : Interfaces.EXCLUDE);
  }

  public static List<Class<?>> hierarchyList(final Class<?> type, boolean includeInterfaces) {
    return IteratorUtils.toList(hierarchy(type, includeInterfaces).iterator());
  }

  public static boolean isConcrete(Class<?> cls) {
    return cls != null && !cls.isInterface() && !Modifier.isAbstract(cls.getModifiers());
  }

  public static boolean isPrimitiveArray(Class<?> clazz) {
    return (clazz.isArray() && clazz.getComponentType().isPrimitive());
  }

  public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
    return ClassUtils.isPrimitiveOrWrapper(clazz);
  }

  public static boolean isPrimitiveWrapper(Class<?> clazz) {
    return ClassUtils.isPrimitiveWrapper(clazz);
  }

  public static boolean isPrimitiveWrapperArray(Class<?> clazz) {
    return (clazz.isArray() && ClassUtils.isPrimitiveWrapper(clazz.getComponentType()));
  }

  public static boolean isSimpleClass(Class<?> clazz) {
    return ClassUtils.isPrimitiveOrWrapper(clazz) || Enum.class.isAssignableFrom(clazz)
        || CharSequence.class.isAssignableFrom(clazz) || Number.class.isAssignableFrom(clazz)
        || Temporal.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz)
        || clazz.equals(Locale.class) || clazz.equals(Class.class) || clazz.equals(Currency.class)
        || clazz.equals(TimeZone.class) || clazz.equals(URI.class) || clazz.equals(URL.class);
  }

  public static void main(String... strings) {
    getClassPathPackageClassNames("org.asosat").forEach(x -> System.out.println(x));
  }

  public static Class<?>[] primitivesToWrappers(final Class<?>... classes) {
    return ClassUtils.primitivesToWrappers(classes);
  }

  public static Class<?> primitiveToWrapper(final Class<?> cls) {
    return ClassUtils.primitiveToWrapper(cls);
  }

  public static Class<?> tryToLoadClassForName(String name) {
    try {
      return forName(name);
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  public static Class<?>[] wrappersToPrimitives(final Class<?>... classes) {
    return ClassUtils.wrappersToPrimitives(classes);
  }

  public static Class<?> wrapperToPrimitive(final Class<?> cls) {
    return ClassUtils.wrapperToPrimitive(cls);
  }

}

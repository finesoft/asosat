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
package org.asosat.kernel.domains.repository;

import static org.asosat.kernel.util.MyBagUtils.asSet;
import static org.asosat.kernel.util.MyClsUtils.getClassPathPackageClassNames;
import static org.asosat.kernel.util.MyClsUtils.hierarchyList;
import static org.asosat.kernel.util.MyClsUtils.tryToLoadClassForName;
import static org.asosat.kernel.util.Preconditions.requireNotBlank;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Set;
import javax.persistence.Converter;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import org.apache.commons.vfs2.FileExtensionSelector;
import org.asosat.kernel.resource.GlobalMessageCodes;
import org.asosat.kernel.resource.MultiClassPathFiles;

/**
 * @author bingo 下午10:13:49
 *
 */
public class JpaUtils {

  static final Set<Class<? extends Annotation>> PERSIS_ANN =
      asSet(Entity.class, Embeddable.class, MappedSuperclass.class, Converter.class);

  public JpaUtils() {}

  public static boolean isPersistenceClass(Class<?> cls) {
    return cls != null && !cls.isInterface() && !Modifier.isAbstract(cls.getModifiers())
        && (PERSIS_ANN.stream().anyMatch(pn -> cls.isAnnotationPresent(pn))
            || hierarchyList(cls, true).stream()
                .anyMatch(c -> PERSIS_ANN.stream().anyMatch(pn -> c.isAnnotationPresent(pn))));
  }

  public static boolean isPersistenceClass(String clsName) {
    return isPersistenceClass(tryToLoadClassForName(clsName));
  }

  public static void stdoutPersistClasses(String pkg) {
    new ArrayList<>(getClassPathPackageClassNames(pkg)).stream()
        .filter(c -> JpaUtils.isPersistenceClass(c)).sorted(String::compareTo)
        .map(x -> new StringBuilder("<class>").append(x).append("</class>").toString())
        .forEach(x -> System.out.println(x));
  }

  public static void stdoutPersistes(String pkg) {
    System.out.println("<!-- mapping files -->");
    stdoutPersistJpaOrmXml(pkg);
    System.out.println("<!-- mapping classes -->");
    stdoutPersistClasses(pkg);
  }

  public static void stdoutPersistJpaOrmXml(String pkg) {
    String packageNameToUse =
        requireNotBlank(pkg, GlobalMessageCodes.ERR_PARAM).replaceAll("\\.", "/");
    MultiClassPathFiles.select(new FileExtensionSelector("xml")).keySet().stream()
        .sorted(String::compareTo).forEach(s -> {
          if (s.contains(packageNameToUse) && s.endsWith("JpaOrm.xml")) {
            System.out.println(new StringBuilder().append("<mapping-file>")
                .append(s.substring(s.indexOf(packageNameToUse))).append("</mapping-file>"));
          }
        });
  }

}

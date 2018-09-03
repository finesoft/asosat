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
package org.asosat.domain.repository;

import static org.asosat.kernel.util.MyBagUtils.asSet;
import static org.asosat.kernel.util.MyClsUtils.hierarchyList;
import static org.asosat.kernel.util.MyClsUtils.tryToLoadClassForName;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Set;
import javax.persistence.Converter;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

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

}

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
package org.asosat.kernel.resource;

import static org.asosat.kernel.util.MyClsUtils.tryToLoadClassForName;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.commons.vfs2.PatternFileSelector;
import org.asosat.kernel.context.DefaultContext;

/**
 * @author bingo 上午10:30:07
 *
 */
@ApplicationScoped
@SuppressWarnings("rawtypes")
public class PropertyEnumerationResource implements EnumerationResource {

  public static final String ENUM_REC_PATH_REG_KEY = "asosat.enum.source.pathRegex";
  public static final String ENUM_REC_LOAD_WAY = "asosat.enum.source.loadWay";
  public static final String DFLT_ENUM_REF_PATH_REG = ".*enum.*\\.properties";

  final Map<Locale, EnumLiteralsObject> holder = new ConcurrentHashMap<>();
  private volatile boolean init = false;
  private volatile boolean lazyLoad = false;

  private String pathRegex = DFLT_ENUM_REF_PATH_REG;

  @Inject
  ConfigResource config;

  public PropertyEnumerationResource() {}

  public static PropertyEnumerationResource instance() {
    return DefaultContext.bean(PropertyEnumerationResource.class);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Class<Enum>> getAllEnumClass() {
    return new ArrayList(this.holder.values().stream()
        .flatMap(e -> e.classLiteral.keySet().stream()).collect(Collectors.toSet()));
  }

  @Override
  public String getEnumClassLiteral(Class<Enum> enumClass, Locale locale) {
    return this.holder.get(locale) == null ? null : (this.holder.get(locale).getLiteral(enumClass));
  }

  @SuppressWarnings("unchecked")
  @Override
  public String getEnumItemLiteral(Enum enumVal, Locale locale) {
    this.load();
    return this.getEnumItemLiterals((Class<Enum>) enumVal.getDeclaringClass(), locale).get(enumVal);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Enum> Map<T, String> getEnumItemLiterals(Class<T> enumClass, Locale locale) {
    this.load();
    Map<Enum, String> lLiterals =
        this.holder.get(locale) == null ? null : (this.holder.get(locale).getLiterals(enumClass));
    return lLiterals == null ? null : new LinkedHashMap(lLiterals);
  }

  public synchronized void reload() {
    this.init = false;
    this.load();
  }

  @SuppressWarnings("unchecked")
  protected void load() {
    if (!this.init) {
      synchronized (this) {
        if (!this.init) {
          try {
            this.destroy();
            MyPropertyResourceBundle.getBundles(new PatternFileSelector(this.pathRegex))
                .forEach((s, res) -> {
                  Locale locale = res.getLocale();
                  EnumLiteralsObject obj =
                      this.holder.computeIfAbsent(locale, (k) -> new EnumLiteralsObject());
                  res.dump().forEach((k, v) -> {
                    int i = k.lastIndexOf(".");
                    String enumClsName = k.substring(0, i), enumItemKey = null;
                    Class enumCls = null;
                    try {
                      enumCls = Class.forName(enumClsName);
                      enumItemKey = k.substring(i + 1);
                    } catch (ClassNotFoundException e) {
                      enumCls = tryToLoadClassForName(k);
                      if (enumCls != null && Enum.class.isAssignableFrom(enumCls)) {
                        obj.putEnumClass(enumCls, v);
                      } else {
                        throw new RuntimeException("enum class " + s + " error");
                      }
                    }
                    if (enumItemKey != null) {
                      obj.putEnum(Enum.valueOf(enumCls, enumItemKey), v);
                    }
                  });
                });
            // TODO validate
          } finally {
            this.init = true;
          }
        }
      }
    }
  }

  @PreDestroy
  synchronized void destroy() {
    this.holder.forEach((k, v) -> {
      v.classLiteral.clear();
      v.enumLiterals.clear();
    });
    this.holder.clear();
  }

  @PostConstruct
  synchronized void init() {
    if (this.config != null) {
      this.pathRegex =
          this.config.getValue(ENUM_REC_PATH_REG_KEY, String.class, DFLT_ENUM_REF_PATH_REG);
      this.lazyLoad = this.config.getValue(ENUM_REC_LOAD_WAY, Boolean.class, false);
    }
    if (!this.lazyLoad) {
      this.load();
    }
  }

  static class EnumLiteralsObject {

    private final Map<Class<Enum>, String> classLiteral = new ConcurrentHashMap<>();
    private final Map<Class<Enum>, Map<Enum, String>> enumLiterals = new ConcurrentHashMap<>();

    public String getLiteral(Class clz) {
      return this.classLiteral.get(clz);
    }

    public Map<Enum, String> getLiterals(Class clz) {
      return this.enumLiterals.get(clz);
    }

    @SuppressWarnings("unchecked")
    public void putEnum(Enum e, String literal) {
      Class declaringClass = e.getDeclaringClass();
      if (!this.enumLiterals.containsKey(declaringClass)) {
        this.enumLiterals.put(declaringClass,
            new TreeMap<Enum, String>((Enum o1, Enum o2) -> o1.ordinal() - o2.ordinal()));
      }
      Map<Enum, String> map = this.enumLiterals.get(declaringClass);
      if (map.put(e, literal) != null) {
        throw new RuntimeException("enum " + e.getClass() + ", value " + e + " is duplicate");
      }
    }

    @SuppressWarnings("unchecked")
    public void putEnumClass(Class clz, String literal) {
      if (this.classLiteral.put(clz, literal) != null) {
        throw new RuntimeException("enum " + clz + ", is duplicate");
      }
    }
  }
}

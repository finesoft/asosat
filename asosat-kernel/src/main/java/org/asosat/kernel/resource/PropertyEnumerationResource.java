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

import static org.asosat.kernel.util.MyBagUtils.asSet;
import static org.asosat.kernel.util.MyClsUtils.tryToLoadClassForName;
import static org.asosat.kernel.util.MyStrUtils.split;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import org.apache.commons.vfs2.PatternFileSelector;
import org.apache.logging.log4j.Logger;
import org.asosat.kernel.annotation.stereotype.InfrastructureServices;
import org.asosat.kernel.context.DefaultContext;
import org.asosat.kernel.util.MyStrUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * @author bingo 上午10:30:07
 *
 */
@ApplicationScoped
@InfrastructureServices
@SuppressWarnings("rawtypes")
public class PropertyEnumerationResource implements EnumerationResource {

  public static PropertyEnumerationResource instance() {
    return DefaultContext.bean(PropertyEnumerationResource.class);
  }

  @Inject
  Logger logger;

  final Map<Locale, EnumLiteralsObject> holder = new ConcurrentHashMap<>();

  private volatile boolean init = false;

  @Inject
  @Any
  @ConfigProperty(name = "asosat.enum.source.path.regex",
      defaultValue = ".*enum([A-Za-z0-9_-]*)\\.properties$")
  String pathRegex;

  @Inject
  @Any
  @ConfigProperty(name = "asosat.enum.source.packages", defaultValue = "org.asosat")
  String pathPackages;

  @Inject
  @Any
  @ConfigProperty(name = "asosat.app.packages", defaultValue = "org.asosat")
  String packages;

  @Inject
  @Any
  @ConfigProperty(name = "asosat.enum.source.load.way", defaultValue = "false")
  volatile boolean lazyLoad = false;

  public PropertyEnumerationResource() {}

  @SuppressWarnings("unchecked")
  @Override
  public List<Class<Enum>> getAllEnumClass() {
    return new ArrayList(this.holder.values().stream()
        .flatMap(e -> e.classLiteral.keySet().stream()).collect(Collectors.toSet()));
  }

  @Override
  public String getEnumClassLiteral(Class<?> enumClass, Locale locale) {
    return this.holder.get(locale) == null ? null : this.holder.get(locale).getLiteral(enumClass);
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
        this.holder.get(locale) == null ? null : this.holder.get(locale).getLiterals(enumClass);
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
            Set<String> pkgs = asSet(split(this.pathPackages, ";"));
            pkgs.addAll(asSet(split(this.packages, ";")));
            pkgs.add("org.asosat");
            pkgs.stream().filter(MyStrUtils::isNotBlank).forEach(pkg -> {
              PropertyResourceBundle
                  .getBundles(pkg,
                      new PatternFileSelector(
                          Pattern.compile(this.pathRegex, Pattern.CASE_INSENSITIVE)))
                  .forEach((s, res) -> {
                    this.logger.info(() -> String.format(
                        "Find enumeration resource, the path is %s, use pattern [%s]", s,
                        this.pathRegex));
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

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
import static org.asosat.kernel.util.MyStrUtils.split;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
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
 *
 * @author bingo 上午12:26:12
 *
 */
@ApplicationScoped
@InfrastructureServices
public class PropertyMessageResource implements MessageResource {

  public static PropertyMessageResource instance() {
    return DefaultContext.bean(PropertyMessageResource.class);
  }

  final Map<Locale, Map<String, MessageFormat>> holder = new ConcurrentHashMap<>(128);

  private volatile boolean init = false;

  @Inject
  Logger logger;

  // .*messages.*\\.properties
  @Inject
  @Any
  @ConfigProperty(name = "asosat.message.source.path.regex",
      defaultValue = ".*message([A-Za-z0-9_-]*)\\.properties$")
  String pathRegex;

  @Inject
  @Any
  @ConfigProperty(name = "asosat.message.source.packages", defaultValue = "org.asosat")
  String pathPackages;

  @Inject
  @Any
  @ConfigProperty(name = "asosat.message.source.load.way", defaultValue = "false")
  volatile boolean lazyLoad = false;

  public PropertyMessageResource() {}

  @Override
  public String getMessage(Locale locale, Object key, Object[] args) throws NoSuchMessageException {
    this.load();
    if (key == null) {
      throw new NoSuchMessageException(null);
    } else {
      Map<String, MessageFormat> mfMap = this.holder.get(locale);
      if (mfMap == null) {
        throw new NoSuchMessageException(key.toString());
      } else {
        MessageFormat mf = mfMap.get(key);
        if (mf == null) {
          throw new NoSuchMessageException(key.toString());
        } else {
          return mf.format(args);
        }
      }
    }
  }

  @Override
  public String getMessage(Locale locale, Object key, Object[] args,
      Function<Locale, String> dfltMsg) {
    this.load();
    if (key == null) {
      return dfltMsg.apply(locale);
    } else {
      Map<String, MessageFormat> mfMap = this.holder.get(locale);
      if (mfMap == null) {
        return dfltMsg.apply(locale);
      } else {
        MessageFormat mf = mfMap.get(key);
        if (mf == null) {
          return dfltMsg.apply(locale);
        } else {
          return mf.format(args);
        }
      }
    }
  }

  public synchronized void reload() {
    this.init = false;
    this.load();
  }

  protected void load() {
    if (!this.init) {
      synchronized (this) {
        if (!this.init) {
          try {
            this.destroy();
            Set<String> pkgs = asSet(split(this.pathPackages, ";"));
            pkgs.add("org.asosat");
            pkgs.stream().filter(MyStrUtils::isNotBlank).forEach(pkg -> {
              PropertyResourceBundle
                  .getBundles(pkg,
                      new PatternFileSelector(
                          Pattern.compile(this.pathRegex, Pattern.CASE_INSENSITIVE)))
                  .forEach((s, res) -> {
                    this.logger.info(() -> String.format(
                        "Find message resource, the path is %s, use pattern [%s]", s,
                        this.pathRegex));
                    Map<String, MessageFormat> localeMap = res.dump().entrySet().stream().collect(
                        Collectors.toMap(k -> k.getKey(), v -> new MessageFormat(v.getValue())));
                    this.holder.computeIfAbsent(res.getLocale(), (k) -> new ConcurrentHashMap<>())
                        .putAll(localeMap);
                  });
            });

          } finally {
            this.init = true;
          }
        }
      }
    }
  }

  @PreDestroy
  synchronized void destroy() {
    this.holder.forEach((k, v) -> v.clear());
    this.holder.clear();
  }

  @PostConstruct
  synchronized void init() {
    if (!this.lazyLoad) {
      this.load();
    }
  }

}

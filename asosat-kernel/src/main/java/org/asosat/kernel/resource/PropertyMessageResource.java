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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.commons.vfs2.PatternFileSelector;
import org.asosat.kernel.context.DefaultContext;
import org.asosat.kernel.normalization.conversion.Conversions;

/**
 *
 * @author bingo 上午12:26:12
 *
 */
@ApplicationScoped
public class PropertyMessageResource implements MessageResource {

  public static final String MSG_REC_PATH_REG_KEY = "asosat.message.source.pathRegex";
  public static final String MSG_REC_LOAD_WAY = "asosat.message.source.loadWay";
  public static final String DFLT_MSG_REF_PATH_REG = ".*messages.*\\.properties";
  final Map<Locale, Map<String, MessageFormat>> holder = new ConcurrentHashMap<>(128);
  private volatile boolean init = false;
  private volatile boolean lazyLoad = false;
  private String pathRegex;

  @Inject
  ConfigResource config;

  public PropertyMessageResource() {}

  public static PropertyMessageResource instance() {
    return DefaultContext.bean(PropertyMessageResource.class);
  }

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
            PropertyResourceBundle.getBundles(new PatternFileSelector(this.pathRegex))
                .forEach((s, res) -> {
                  Map<String, MessageFormat> localeMap = res.dump().entrySet().stream().collect(
                      Collectors.toMap(k -> k.getKey(), v -> new MessageFormat(v.getValue())));
                  this.holder.computeIfAbsent(res.getLocale(), (k) -> new ConcurrentHashMap<>())
                      .putAll(localeMap);
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
    if (this.config != null) {
      this.pathRegex =
          this.config.getValue(MSG_REC_PATH_REG_KEY, Conversions::toString, DFLT_MSG_REF_PATH_REG);
      this.lazyLoad = this.config.getValue(MSG_REC_LOAD_WAY, Conversions::toBoolean, false);
    }
    if (!this.lazyLoad) {
      this.load();
    }
  }

}

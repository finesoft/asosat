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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import org.apache.commons.vfs2.PatternFileSelector;
import org.asosat.kernel.context.DefaultContext;

/**
 * @author bingo 下午2:53:26
 *
 */
@ApplicationScoped
public class PropertyConfigResource implements ConfigResource {

  public static final String DFLT_MSG_REF_PATH_REG = ".*config.*\\.properties";

  final Map<String, Map<String, Object>> holder = new ConcurrentHashMap<>();
  private volatile boolean init = false;

  public PropertyConfigResource() {}

  public static PropertyConfigResource instance() {
    return DefaultContext.bean(PropertyConfigResource.class);
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public Map<String, String> getProperties() {
    return null;
  }

  @Override
  public <T> T getValue(String propertyName, Class<T> cls) {
    return null;
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
            MyPropertyResourceBundle.getBundles(new PatternFileSelector(DFLT_MSG_REF_PATH_REG))
                .forEach((s, res) -> {
                  this.holder
                      .computeIfAbsent(res.getBaseBundleName(), (k) -> new ConcurrentHashMap<>())
                      .putAll(res.dump());
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
    this.load();
  }
}

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
package org.asosat.kernel.config;

import static org.asosat.kernel.util.MyMapUtils.getMapObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import org.apache.commons.vfs2.PatternFileSelector;
import org.asosat.kernel.context.DefaultContext;
import org.asosat.kernel.resource.PropertyResourceBundle;

/**
 * @author bingo 下午2:53:26
 *
 */
@ApplicationScoped
public class PropertyConfigResource {

  public static final String DFLT_CFG_REF_PATH_REG =
      ".*config.*\\.properties;.*application.*\\.properties";

  public static PropertyConfigResource instance() {
    return DefaultContext.bean(PropertyConfigResource.class);
  }

  final Map<String, String> holder = new ConcurrentHashMap<>();

  private volatile boolean init = false;

  public PropertyConfigResource() {}


  public Map<String, String> getProperties() {
    return Collections.unmodifiableMap(this.holder);
  }

  public <T> T getValue(String propertyName, final Function<Object, T> extractor) {
    return getMapObject(this.holder, propertyName, extractor);
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
            Arrays.stream(DFLT_CFG_REF_PATH_REG.split(";")).forEach(fn -> PropertyResourceBundle
                .getBundles(new PatternFileSelector(fn)).forEach((s, res) -> {
                  this.holder.putAll(res.dump());
                }));
          } finally {
            this.init = true;
          }
        }
      }
    }
  }

  @PreDestroy
  synchronized void destroy() {
    this.holder.clear();
  }

  @PostConstruct
  synchronized void init() {
    this.load();
  }
}

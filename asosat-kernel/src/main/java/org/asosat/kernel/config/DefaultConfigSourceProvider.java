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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.vfs2.PatternFileSelector;
import org.asosat.kernel.resource.PropertyResourceBundle;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

/**
 * asosat-kernel
 *
 * @author bingo 下午7:07:00
 *
 */
public class DefaultConfigSourceProvider implements ConfigSourceProvider {

  public static final String DFLT_CFG_REF_PATH_REG =
      ".*config.*\\.properties;.*application.*\\.properties";

  @Override
  public Iterable<ConfigSource> getConfigSources(ClassLoader forClassLoader) {
    List<ConfigSource> list = new ArrayList<>();
    Arrays.stream(DFLT_CFG_REF_PATH_REG.split(";")).forEach(
        fn -> PropertyResourceBundle.getBundles(new PatternFileSelector(fn)).forEach((s, res) -> {
          list.add(new ConfigSource() {
            final Map<String, String> map = Collections.unmodifiableMap(new HashMap<>(res.dump()));
            final String name = s;
            final int ordinal = DEFAULT_ORDINAL * 10;

            @Override
            public String getName() {
              return this.name;
            }

            @Override
            public int getOrdinal() {
              return this.ordinal;
            }

            @Override
            public Map<String, String> getProperties() {
              return this.map;
            }

            @Override
            public String getValue(String propertyName) {
              return this.map.get(propertyName);
            }

          });
        }));
    return list;
  }

}

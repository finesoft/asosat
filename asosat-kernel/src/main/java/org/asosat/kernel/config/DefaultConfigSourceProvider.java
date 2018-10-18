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
import java.util.regex.Pattern;
import org.apache.commons.vfs2.PatternFileSelector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

  public static final String[] DFLT_PATH_REGEX = {".*config([A-Za-z0-9_-]*)\\.properties$",
      ".*application([A-Za-z0-9_-]*)\\.properties$", ".*setting([A-Za-z0-9_-]*)\\.properties$"};

  @Override
  public Iterable<ConfigSource> getConfigSources(ClassLoader forClassLoader) {
    final List<ConfigSource> list = new ArrayList<>();
    Arrays.stream(DFLT_PATH_REGEX)
        .forEach(regex -> PropertyResourceBundle
            .getBundles(new PatternFileSelector(regex, Pattern.CASE_INSENSITIVE))
            .forEach((s, res) -> list.add(new PropertyConfigSource(s, res))));
    return list;
  }

  public static class PropertyConfigSource implements ConfigSource {

    final Map<String, String> map;
    final String name;
    final int ordinal = DEFAULT_ORDINAL * 10;

    Logger logger = LogManager.getLogger(PropertyConfigSource.class.getName());

    PropertyConfigSource(String name, PropertyResourceBundle properties) {
      this.logger.debug(() -> String.format("Find config resource, the path is %s", name));
      this.map = Collections.unmodifiableMap(new HashMap<>(properties.dump()));
      this.name = name;
    }

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

  }

}

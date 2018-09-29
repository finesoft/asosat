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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.asosat.kernel.normal.conversion.Conversions;
import org.asosat.kernel.normal.conversion.Convertors;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;
import io.smallrye.config.SmallRyeConfig;

/**
 * asosat-kernel
 *
 * @author bingo 下午4:54:00
 *
 */
public class DefaultConfig extends SmallRyeConfig {

  private static final long serialVersionUID = 112340469370704785L;

  @SuppressWarnings("rawtypes")
  static Map<Type, Converter> converters = new HashMap<>();

  static {
    Convertors.instance().getSupportTypes()
        .forEach(t -> converters.put(t, (o) -> Conversions.getConvertor(t).convert(t, o)));
  }

  /**
   * @param configSources
   */
  protected DefaultConfig(List<ConfigSource> configSources) {
    super(configSources, converters);
  }

}

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
import java.util.List;
import java.util.Map;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;
import io.smallrye.config.ConfigFactory;

/**
 * asosat-kernel
 *
 * @author bingo 下午6:10:11
 *
 */
public abstract class AbstractConfigFactory implements ConfigFactory {

  @Override
  public Config newConfig(List<ConfigSource> sources,
      @SuppressWarnings("rawtypes") Map<Type, Converter> configConverters) {
    return new DefaultConfig(sources);
  }

}

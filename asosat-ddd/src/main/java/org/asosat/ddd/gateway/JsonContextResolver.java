/*
 * Copyright (c) 2013-2018, Bingo.Chen (finesoft@gmail.com).
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
package org.asosat.ddd.gateway;

import static org.corant.shared.util.MapUtils.mapOf;
import static org.corant.shared.util.ObjectUtils.defaultObject;
import static org.corant.suites.cdi.Instances.select;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import org.corant.Corant;
import org.corant.suites.bundle.EnumerationBundle;
import org.corant.suites.json.JsonUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * corant-asosat-ddd
 *
 * @author bingo 下午4:49:32
 */
@ApplicationScoped
@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JsonContextResolver implements ContextResolver<ObjectMapper> {

  public final static Long BROWSER_SAFE_LONG = 9007199254740991L;
  public final static BigInteger BROWSER_SAFE_BIGINTEGER = BigInteger.valueOf(BROWSER_SAFE_LONG);

  private final static ObjectMapper objectMapper;
  static {
    SimpleModule simpleModule = new SimpleModule().addSerializer(new BigIntegerJsonSerializer())
        .addSerializer(new LongJsonSerializer()).addSerializer(new EnumJsonSerializer());
    objectMapper = JsonUtils.copyMapper().registerModules(simpleModule);
  }

  @Inject
  @Any
  Instance<JsonContextResolverConfigurator> configurator;

  @Override
  public ObjectMapper getContext(Class<?> objectType) {
    return objectMapper;
  }

  @PostConstruct
  void onPostConstruct() {
    if (!configurator.isUnsatisfied()) {
      configurator.forEach(cfg -> cfg.config(objectMapper));
    }
  }

  static final class BigIntegerJsonSerializer extends JsonSerializer<BigInteger> {
    @Override
    public Class<BigInteger> handledType() {
      return BigInteger.class;
    }

    @Override
    public void serialize(BigInteger value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      if (value.compareTo(BROWSER_SAFE_BIGINTEGER) > 0) {
        gen.writeString(value.toString());
      } else {
        gen.writeNumber(value);
      }
    }
  }

  @Deprecated
  @SuppressWarnings("rawtypes")
  static final class EnumJsonSerializer extends JsonSerializer<Enum> {

    static final Map<Enum, Map<String, Object>> CACHES = new ConcurrentHashMap<>();

    static volatile EnumerationBundle bundle;

    @Override
    public Class<Enum> handledType() {
      return Enum.class;
    }

    @Override
    public void serialize(Enum value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      gen.writeObject(resolveEnumLiteral(value));
    }

    EnumerationBundle resolveBundle() {
      if (bundle == null) {
        synchronized (this) {
          if (bundle == null && Corant.current().isRuning()
              && select(EnumerationBundle.class).isResolvable()) {
            bundle = select(EnumerationBundle.class).get();
          } else {
            bundle = new EnumJsonSerializerBundle();
          }
        }
      }
      return bundle;
    }

    Map<String, Object> resolveEnumLiteral(Enum value) {
      return CACHES.computeIfAbsent(value, (v) -> {
        String literal = resolveBundle().getEnumItemLiteral(value, Locale.getDefault());
        return mapOf("name", value.name(), "literal", defaultObject(literal, value.name()), "class",
            value.getDeclaringClass().getName(), "ordinal", value.ordinal());
      });
    }
  }

  @Deprecated
  @SuppressWarnings("rawtypes")
  static final class EnumJsonSerializerBundle implements EnumerationBundle {
    @Override
    public String getEnumItemLiteral(Enum enumVal, Locale locale) {
      return enumVal.name();
    }
  }

  static final class LongJsonSerializer extends JsonSerializer<Long> {
    @Override
    public Class<Long> handledType() {
      return Long.class;
    }

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      if (value.compareTo(BROWSER_SAFE_LONG) > 0) {
        gen.writeString(value.toString());
      } else {
        gen.writeNumber(value);
      }
    }
  }
}

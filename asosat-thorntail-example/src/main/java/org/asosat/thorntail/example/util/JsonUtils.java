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
package org.asosat.thorntail.example.util;

import static org.asosat.kernel.util.MyStrUtils.isNotBlank;
import java.io.IOException;
import java.util.Map;
import org.asosat.kernel.exception.GeneralRuntimeException;
import org.asosat.kernel.resource.GlobalMessageCodes;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.fasterxml.jackson.databind.ser.std.SqlDateSerializer;

/**
 * @author bingo 下午5:25:09
 *
 */
public class JsonUtils {

  private final static ObjectMapper objectMapper;

  static {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new SimpleModule().addSerializer(new SqlDateSerializer()));
    objectMapper.getSerializerProvider().setNullKeySerializer(NullSerializer.instance);
    objectMapper.enable(Feature.ALLOW_COMMENTS);
    objectMapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
    objectMapper.disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
    objectMapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
    objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
  }

  public JsonUtils() {}

  /**
   * @return
   */
  public static ObjectMapper copyMapper() {
    return objectMapper.copy();
  }

  /**
   * @param cmd
   * @return
   * @throws IOException
   * @throws JsonMappingException
   * @throws JsonParseException
   */
  @SuppressWarnings("unchecked")
  public static <K, V> Map<K, V> deserialize(String cmd) {
    return deserialize(cmd, Map.class);
  }

  /**
   * @param <C>
   * @param <C>
   * @param <E>
   * @param cmd
   * @return
   * @throws IOException
   * @throws JsonMappingException
   * @throws JsonParseException
   */
  @SafeVarargs
  public static <C, E> C deserialize(String cmd, Class<C> parametrized,
      Class<E>... parameterClasses) {
    if (!isNotBlank(cmd)) {
      return null;
    }
    try {
      return objectMapper.readValue(cmd,
          objectMapper.getTypeFactory().constructParametricType(parametrized, parameterClasses));
    } catch (IOException e) {
      throw new GeneralRuntimeException(e.getCause(), GlobalMessageCodes.ERR_OBJ_SEL,
          "\"" + cmd + "\"");
    }
  }

  /**
   * @param cmd
   * @param clazz
   * @return
   */
  public static <T> T deserialize(String cmd, Class<T> clazz) {
    if (isNotBlank(cmd)) {
      try {
        return objectMapper.readValue(cmd, clazz);
      } catch (IOException e) {
        throw new GeneralRuntimeException(e.getCause(), GlobalMessageCodes.ERR_OBJ_SEL,
            "\"" + cmd + "\"", clazz.getClass().getName());
      }
    }
    return null;
  }

  public static <K, V> Map<K, V> deserializeMap(String cmd, Class<K> keyCls, Class<V> valueCls) {
    if (!isNotBlank(cmd)) {
      return null;
    }
    try {
      return objectMapper.readValue(cmd,
          objectMapper.getTypeFactory().constructParametricType(Map.class, keyCls, valueCls));
    } catch (IOException e) {
      throw new GeneralRuntimeException(e.getCause(), GlobalMessageCodes.ERR_OBJ_SEL,
          "\"" + cmd + "\"");
    }
  }

  public static ObjectMapper referenceMapper() {
    return objectMapper;
  }


  public static String serialize(Object obj) {
    if (obj != null) {
      try {
        return objectMapper.writeValueAsString(obj);
      } catch (JsonProcessingException e) {
        throw new GeneralRuntimeException(e.getCause(), GlobalMessageCodes.ERR_OBJ_SEL,
            "[" + obj + "]");
      }
    }
    return null;
  }

}

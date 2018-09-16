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
package org.asosat.kernel.normalization.conversion;

import static org.asosat.kernel.util.MyClsUtils.isSimpleClass;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.BigIntegerConverter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.ByteConverter;
import org.apache.commons.beanutils.converters.CharacterConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;
import org.apache.commons.beanutils.converters.StringConverter;
import org.apache.commons.lang3.StringUtils;
import org.asosat.kernel.exception.KernelRuntimeException;
import org.asosat.kernel.normalization.conversion.convertor.CurrencyConvertor;
import org.asosat.kernel.normalization.conversion.convertor.EnumConvertor;
import org.asosat.kernel.normalization.conversion.convertor.InstantConvertor;
import org.asosat.kernel.normalization.conversion.convertor.LocalDateConvertor;
import org.asosat.kernel.normalization.conversion.convertor.TimeZoneConvertor;
import org.asosat.kernel.normalization.conversion.convertor.ZonedDateTimeConvertor;
import org.asosat.kernel.util.MyBagUtils;
import org.asosat.kernel.util.MyObjUtils;

/**
 * @author bingo 上午12:29:05
 *
 */
public class Conversions {

  static ConvertUtilsBean provider = new ConvertUtilsBean();

  static Convertor enumConvertor = new EnumConvertor();

  static {
    synchronized (provider) {
      provider.register(true, false, 0);
      provider.register(new BooleanConverter(new String[] {"true", "yes", "y", "on", "1", "是"},
          new String[] {"false", "no", "n", "off", "0", "否"}, false), Boolean.class);
      provider.register(new ByteConverter(null), Byte.class);
      provider.register(new CharacterConverter(null), Character.class);
      provider.register(new DoubleConverter(null), Double.class);
      provider.register(new FloatConverter(null), Float.class);
      provider.register(new IntegerConverter(null), Integer.class);
      provider.register(new LongConverter(null), Long.class);
      provider.register(new ShortConverter(null), Short.class);
      provider.register(new StringConverter(null), String.class);
      provider.register(new BigDecimalConverter(null), BigDecimal.class);
      provider.register(new BigIntegerConverter(null), BigInteger.class);
      provider.register(new InstantConvertor(), Instant.class);
      provider.register(new LocalDateConvertor(), LocalDate.class);
      provider.register(new CurrencyConvertor(), Currency.class);
      provider.register(new TimeZoneConvertor(), TimeZone.class);
      provider.register(new ZonedDateTimeConvertor(), ZonedDateTime.class);
    }
  }

  protected Conversions() {}


  public static void main(String... dfltVal) {
    System.out.println(Conversions.toBigDecimal("123.45"));
    System.out.println(Conversions.toBigInteger("12312"));
    System.out.println(Conversions.toBoolean("否"));
  }

  public static BigDecimal toBigDecimal(Object obj) {
    return toBigDecimal(obj, null);
  }

  public static BigDecimal toBigDecimal(Object obj, BigDecimal dfltVal) {
    Object val = provider.convert(obj, BigDecimal.class);
    return val != null ? BigDecimal.class.cast(val) : dfltVal;
  }

  public static List<BigDecimal> toBigDecimalList(Object obj) {
    return toList(obj, Conversions::toBigDecimal);
  }

  public static BigInteger toBigInteger(Object obj) {
    return toBigInteger(obj, null);
  }

  public static BigInteger toBigInteger(Object obj, BigInteger dfltVal) {
    Object val = provider.convert(obj, BigInteger.class);
    return val != null ? BigInteger.class.cast(val) : dfltVal;
  }

  public static List<BigInteger> toBigIntegerList(Object obj) {
    return toList(obj, Conversions::toBigInteger);
  }

  public static Boolean toBoolean(Object obj) {
    Object val = provider.convert(obj, Boolean.class);
    return val == null ? Boolean.FALSE : Boolean.class.cast(val);
  }

  public static Character toCharacter(Object obj) {
    return Character.class.cast(provider.convert(obj, Character.class));
  }

  public static Currency toCurrency(Object obj) {
    return toCurrency(obj, null);
  }

  public static Currency toCurrency(Object obj, Currency dfltVal) {
    Object val = provider.convert(obj, Currency.class);
    return val != null ? Currency.class.cast(val) : dfltVal;
  }

  public static Double toDouble(Object obj) {
    return toDouble(obj, null);
  }

  public static Double toDouble(Object obj, Double dfltVal) {
    Object val = provider.convert(obj, Double.class);
    return val != null ? Double.class.cast(val) : dfltVal;
  }

  public static List<Double> toDoubleList(Object obj) {
    return toList(obj, Conversions::toDouble);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Enum<T>> T toEnum(Object obj, Class<T> enumClazz) {
    autoRegisterEnumConvertor(enumClazz);
    Object val = provider.convert(obj, enumClazz);
    return val != null ? (T) val : null;
  }

  public static <T extends Enum<T>> List<T> toEnumList(Object obj, Class<T> enumClazz) {
    List<T> list = toList(obj, e -> Conversions.toEnum(e, enumClazz));
    if (list.isEmpty() && obj != null) {
      final Deque<StringBuilder> tpl = new LinkedList<>();
      tpl.add(new StringBuilder());
      obj.toString().chars().forEach(c -> {
        if (Character.isDigit(c) || Character.isAlphabetic(c) || c == '_') {
          tpl.peekLast().append((char) c);
        } else {
          tpl.offer(new StringBuilder());
        }
      });
      List<String> tpls = tpl.stream().filter(p -> p.length() > 0).map(MyObjUtils::toString)
          .collect(Collectors.toList());
      tpl.clear();
      if (tpls.stream()
          .anyMatch(p -> p.chars().anyMatch(m -> Character.isAlphabetic(m) || m == '_'))) {
        list = tpls.stream().map(m -> Enum.valueOf(enumClazz, m)).collect(Collectors.toList());
      } else {
        list = tpls.stream().map(m -> enumClazz.getEnumConstants()[Integer.parseInt(m)])
            .collect(Collectors.toList());
      }
      tpls.clear();
    }
    return list;
  }

  public static Float toFloat(Object obj) {
    return toFloat(obj, null);
  }

  public static Float toFloat(Object obj, Float dfltVal) {
    Object val = provider.convert(obj, Float.class);
    return val != null ? Float.class.cast(val) : dfltVal;
  }

  public static List<Float> toFloatList(Object obj) {
    return toList(obj, Conversions::toFloat);
  }

  public static Instant toInstant(Object obj) {
    return toInstant(obj, null);
  }

  public static Instant toInstant(Object obj, Instant dfltVal) {
    Object val = provider.convert(obj, Instant.class);
    return val != null ? Instant.class.cast(val) : dfltVal;
  }

  public static List<Instant> toInstantList(Object obj) {
    return toList(obj, Conversions::toInstant);
  }

  public static Integer toInteger(Object obj) {
    return toInteger(obj, null);
  }

  public static Integer toInteger(Object obj, Integer dfltVal) {
    Object val = provider.convert(obj, Integer.class);
    return val != null ? Integer.class.cast(val) : dfltVal;
  }

  public static List<Integer> toIntegerList(Object obj) {
    return toList(obj, Conversions::toInteger);
  }

  public static <T> List<T> toList(Object obj, Function<Object, T> convert) {
    List<T> values = new ArrayList<>();
    if (obj instanceof Collection<?>) {
      values = ((Collection<?>) obj).stream().map(convert).collect(Collectors.toList());
    } else if (obj instanceof Object[]) {
      values = Arrays.stream((Object[]) obj).map(convert).collect(Collectors.toList());
    }
    return values;
  }

  public static LocalDate toLocalDate(Object obj) {
    return toLocalDate(obj, null);
  }

  public static LocalDate toLocalDate(Object obj, LocalDate dfltVal) {
    Object val = provider.convert(obj, LocalDate.class);
    return val != null ? LocalDate.class.cast(val) : dfltVal;
  }

  public static List<LocalDate> toLocalDateList(Object obj) {
    return toList(obj, Conversions::toLocalDate);
  }

  public static Locale toLocale(Object obj) {
    Object val = provider.convert(obj, Locale.class);
    return val != null ? Locale.class.cast(val) : null;
  }

  public static Locale toLocale(Object obj, Locale elseVal) {
    Locale value = toLocale(obj);
    return value != null ? value : elseVal;
  }

  public static Long toLong(Object obj) {
    return toLong(obj, null);
  }

  public static Long toLong(Object obj, Long dfltVal) {
    Object val = provider.convert(obj, Long.class);
    return val != null ? Long.class.cast(val) : dfltVal;
  }

  public static List<Long> toLongList(Object obj) {
    return toList(obj, Conversions::toLong);
  }

  public static Map<String, Object> toMap(Object obj, String... unLoadProperties) {
    return MapConvertor.beanToMap(obj, unLoadProperties);
  }

  @SuppressWarnings("unchecked")
  public static <T> T toObject(Object obj) {
    return obj == null ? null : (T) obj;
  }

  public static Short toShort(Object obj) {
    return toShort(obj, null);
  }

  public static Short toShort(Object obj, Short dfltVal) {
    Object val = provider.convert(obj, Short.class);
    return val != null ? Short.class.cast(val) : dfltVal;
  }

  public static List<Short> toShortList(Object obj) {
    return toList(obj, Conversions::toShort);
  }

  public static String toString(Object obj) {
    return obj == null ? null : obj.toString();
  }

  public static TimeZone toTimeZone(Object obj) {
    Object val = provider.convert(obj, TimeZone.class);
    return val != null ? TimeZone.class.cast(val) : null;
  }

  public static ZonedDateTime toZonedDateTime(Object obj) {
    return toZonedDateTime(obj, null);
  }

  public static ZonedDateTime toZonedDateTime(Object obj, ZonedDateTime dfltVal) {
    Object val = provider.convert(obj, ZonedDateTime.class);
    return val != null ? ZonedDateTime.class.cast(val) : dfltVal;
  }

  public static List<ZonedDateTime> toZonedDateTimeList(Object obj) {
    return toList(obj, Conversions::toZonedDateTime);
  }

  static void autoRegisterEnumConvertor(Class<?> clazz) {
    if (clazz != null && clazz.isEnum() && Conversions.provider.lookup(clazz) == null) {
      synchronized (provider) {
        if (provider.lookup(clazz) == null) {
          provider.register(enumConvertor, clazz);
        }
      }
    }
  }


  @SuppressWarnings({"unchecked", "rawtypes"})
  public static class MapConvertor {

    private static final Logger logger = Logger.getLogger(MapConvertor.class.toString());

    private static final ConcurrentHashMap<Class, List<Field>> cacheClassProperty =
        new ConcurrentHashMap<>();

    private static final int DLFT_LOOP_DEEP = 8;

    private MapConvertor() {

    }

    /**
     * 转换集合对象为Map
     *
     * @param list 待转换的对象
     * @param maxLoopDeep 遍历深度，默认为6
     * @param unLoadProperties 不加载的属性
     * @return
     */
    public static List beanToMap(Collection<?> list, int maxLoopDeep, String... unLoadProperties) {
      if (list == null) {
        return new ArrayList<>();
      }
      Set<String> unLoad = new HashSet<>();
      Map<Integer, List<Object>> hashStack = new HashMap<>();
      Map<Object, Object> temp = new HashMap<>();
      if (unLoadProperties != null && unLoadProperties.length > 0) {
        for (String s : unLoadProperties) {
          unLoad.add(s);
        }
      }
      unLoad.add(".*class");
      List result;
      try {
        result = beanToMap(list, hashStack, unLoad, "", temp, 1,
            maxLoopDeep < 1 ? DLFT_LOOP_DEEP : maxLoopDeep);
      } catch (Exception e) {
        throw new KernelRuntimeException(e);
      }
      temp.clear();
      hashStack.clear();
      unLoad.clear();
      return result;
    }

    /**
     * 转换集合对象
     *
     * @param list
     * @param unLoadProperties
     * @return
     */
    public static List beanToMap(Collection<?> list, String... unLoadProperties) {
      return beanToMap(list, DLFT_LOOP_DEEP, unLoadProperties);
    }

    /**
     * 转换对象为Map，基本类型对象不转换
     *
     * @param obj 待转换的对象
     * @param maxLoopDeep 遍历深度，默认为6
     * @param unLoadProperties 不加载的属性
     * @return
     */
    public static Map<String, Object> beanToMap(Object obj, int maxLoopDeep,
        String... unLoadProperties) {
      if (obj == null) {
        return null;
      } else if (isSimpleClass(obj.getClass())) {
        return null;
      } else if (obj instanceof Collection) {
        throw new KernelRuntimeException("Unsupport transform class type : Collection");
      } else {
        Set<String> unLoad = new HashSet<>();
        Map<Integer, List<Object>> hashStack = new HashMap<>();
        Map<Object, Object> temp = new HashMap<>();
        if (unLoadProperties != null && unLoadProperties.length > 0) {
          for (String s : unLoadProperties) {
            unLoad.add(s);
          }
        }
        Map<String, Object> rObj = null;
        unLoad.add(".*class");
        try {
          if (obj instanceof Map) {
            rObj = beanToMap((Map) obj, hashStack, unLoad, "", temp, 1,
                maxLoopDeep < 1 ? DLFT_LOOP_DEEP : maxLoopDeep);
          } else {
            rObj = beanToMap(obj, hashStack, unLoad, "", temp, 1,
                maxLoopDeep < 1 ? DLFT_LOOP_DEEP : maxLoopDeep);
          }
        } catch (Exception e) {
          throw new KernelRuntimeException(e);
        }
        temp.clear();
        hashStack.clear();
        unLoad.clear();
        return rObj;
      }
    }

    /**
     *
     * @param obj 需要转换的对象
     * @param unLoadProperties 不加载的属性,默认转换未加载的延迟加载项 例：[不加载parent的child属性传入参数: parent\\.child]
     *        [不加载全部name属性 : .*name] [不加载父类属性 ：.*\\..*]
     * @return
     */
    public static Map<String, Object> beanToMap(Object obj, String... unLoadProperties) {
      return beanToMap(obj, DLFT_LOOP_DEEP, unLoadProperties);
    }


    // List判断是否加载在调用处已处理
    private static List beanToMap(Collection<?> list, Map<Integer, List<Object>> hashStack,
        Set<String> unLoadProperties, String prefix, Map<Object, Object> temp, int deep,
        int maxLoopDeep)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
      List result = new ArrayList<>();
      if (deep == maxLoopDeep) {
        return result;// TODO : 是否加载Id
      }
      for (Object obj : list) {
        if (obj == null) {
          continue;
        } else if (isSimpleClass(obj.getClass())) {
          // 简单类型
          result.add(obj);
        } else if (obj instanceof Collection) {
          Collection<?> c = (Collection<?>) obj;
          result
              .add(beanToMap(c, hashStack, unLoadProperties, prefix, temp, deep + 1, maxLoopDeep));
        } else if (obj instanceof Object[]) {
          Collection<?> c = Arrays.asList((Object[]) obj);
          result
              .add(beanToMap(c, hashStack, unLoadProperties, prefix, temp, deep + 1, maxLoopDeep));
        } else if (obj instanceof Map) {
          Map m = (Map) obj;
          result
              .add(beanToMap(m, hashStack, unLoadProperties, prefix, temp, deep + 1, maxLoopDeep));
        } else {
          // 复杂类型
          if (judgeRepeat(hashStack, obj)) {
            // 重复不处理
            if (temp.get(obj) == null) {
              Map<String, Object> map =
                  beanToMap(obj, hashStack, unLoadProperties, prefix, temp, deep + 1, maxLoopDeep);
              if (map != null) {
                result.add(map);
              }
            } else {
              result.add(temp.get(obj));
            }
          } else {
            Map<String, Object> map =
                beanToMap(obj, hashStack, unLoadProperties, prefix, temp, deep + 1, maxLoopDeep);
            if (map != null) {
              temp.put(obj, map);
              result.add(temp.get(obj));
            }
          }
        }
      }
      return result;
    }

    private static Map beanToMap(Map map, Map<Integer, List<Object>> hashStack,
        Set<String> unLoadProperties, String prefix, Map<Object, Object> temp, int deep,
        int maxLoopDeep)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
      Map<Object, Object> result = new HashMap<>();
      if (deep == maxLoopDeep) {
        // TODO : 是否加载Id
        return result;
      }
      for (Iterator<Entry<Object, Object>> it = map.entrySet().iterator(); it.hasNext();) {
        Entry<Object, Object> next = it.next();
        Object obj = next.getValue();
        // 判断是否加载
        String fullName = StringUtils.isNotBlank(prefix) ? prefix + "." + next.getKey().toString()
            : next.getKey().toString();
        if (judgeUnLoadProperty(fullName, unLoadProperties)) {
          continue;
        }

        if (obj == null) {
          result.put(next.getKey(), null);
        } else if (isSimpleClass(obj.getClass())) {
          // 简单类型
          result.put(next.getKey(), obj);
        } else if (obj instanceof Collection) {
          Collection<?> c = (Collection<?>) obj;
          result.put(next.getKey(),
              beanToMap(c, hashStack, unLoadProperties, fullName, temp, deep + 1, maxLoopDeep));
        } else if (obj instanceof Object[]) {
          Collection<?> c = Arrays.asList((Object[]) obj);
          result.put(next.getKey(),
              beanToMap(c, hashStack, unLoadProperties, fullName, temp, deep + 1, maxLoopDeep));
        } else if (obj instanceof Map) {
          Map m = (Map) obj;
          result.put(next.getKey(),
              beanToMap(m, hashStack, unLoadProperties, fullName, temp, deep + 1, maxLoopDeep));
        } else {
          // 复杂类型
          if (judgeRepeat(hashStack, obj)) {
            // 重复不处理
            if (temp.get(obj) == null) {
              result.put(next.getKey(),
                  beanToMap(obj, hashStack, unLoadProperties, prefix, temp, deep + 1, maxLoopDeep));
            } else {
              result.put(next.getKey(), temp.get(obj));
            }
          } else {
            temp.put(obj,
                beanToMap(obj, hashStack, unLoadProperties, fullName, temp, deep + 1, maxLoopDeep));
            result.put(next.getKey(), temp.get(obj));
          }
        }
      }
      return result;
    }

    private static Map<String, Object> beanToMap(Object obj, Map<Integer, List<Object>> hashStack,
        Set<String> unLoadProperties, String prefix, Map<Object, Object> temp, int deep,
        int maxLoopDeep)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
      if (obj == null || isSimpleClass(obj.getClass()) || (obj instanceof Map)
          || (obj instanceof Collection) || (obj instanceof Object[])) {
        throw new KernelRuntimeException(
            " Unsupport transform class type :" + (obj == null ? "null" : obj.getClass()));
      }
      if (deep == maxLoopDeep) {
        return null;// TODO : 是否加载Id
      } else {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        List<Field> fields = getClassAllFields(clazz);
        for (Field f : fields) {
          String propertyName = f.getName();
          String newPreFix =
              StringUtils.isNotBlank(prefix) ? prefix + "." + propertyName : propertyName;
          if (judgeUnLoadProperty(newPreFix, unLoadProperties)) {
            continue;
          } else {
            try {
              Object property = f.get(obj);
              // 空或基本类型
              if (property == null || isSimpleClass(property.getClass())) {
                map.put(propertyName, property);
                // 延迟加载
              } else if (property instanceof Object[]) {
                Collection<?> c = Arrays.asList((Object[]) property);
                map.put(propertyName, beanToMap(c, hashStack, unLoadProperties, newPreFix, temp,
                    deep + 1, maxLoopDeep));
              } else if (property instanceof Collection) {
                Collection<?> c = (Collection<?>) property;
                map.put(propertyName, beanToMap(c, hashStack, unLoadProperties, newPreFix, temp,
                    deep + 1, maxLoopDeep));
              } else if (property instanceof Map) {
                Map m = (Map) property;
                map.put(propertyName, beanToMap(m, hashStack, unLoadProperties, newPreFix, temp,
                    deep + 1, maxLoopDeep));
              } else {
                if (judgeRepeat(hashStack, property)) {
                  // 重复不处理
                  if (temp.get(property) == null) {
                    map.put(propertyName, beanToMap(property, hashStack, unLoadProperties, prefix,
                        temp, deep + 1, maxLoopDeep));
                  } else {
                    map.put(propertyName, temp.get(property));
                  }
                } else {
                  temp.put(property, beanToMap(property, hashStack, unLoadProperties, newPreFix,
                      temp, deep + 1, maxLoopDeep));
                  map.put(propertyName, temp.get(property));
                }
              }
            } catch (IllegalAccessException | IllegalArgumentException e) {
              logger.log(Level.WARNING, e, () -> "");
            }
          }
        }
        return map;
      }
    }

    /**
     * 获取类的所有属性的描述
     *
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private static List<Field> getClassAllFields(Class clazz)
        throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
      if (cacheClassProperty.containsKey(clazz)) {
        return cacheClassProperty.get(clazz);
      }
      List<Field> fields = new ArrayList<>();
      List<Class> clazzes = new ArrayList<>();
      clazzes.add(clazz);
      Class parent = clazz;
      while ((parent = parent.getSuperclass()) != null) {
        clazzes.add(parent);
      }
      for (Class c : clazzes) {
        for (Field f : c.getDeclaredFields()) {
          // 该属性有get方法才取
          if (!Modifier.isTransient(f.getModifiers())
              && BeanUtils.getProperty(clazz, f.getName()) != null) {
            fields.add(f);
            f.setAccessible(true);
          }
        }
      }
      List<Field> actFields = cacheClassProperty.putIfAbsent(clazz, fields);
      if (actFields == null) {
        actFields = fields;
      }
      return actFields;
    }

    /**
     * 栈中是否含有该对象引用
     *
     * @param hashStack
     * @param obj
     * @return
     */
    private static boolean judgeRepeat(Map<Integer, List<Object>> hashStack, Object obj) {
      Integer key = obj.hashCode();
      if (hashStack.containsKey(key)) {
        for (Object o : hashStack.get(key)) {
          if (o == obj) {
            return true;
          }
        }
        hashStack.get(key).add(obj);
        return false;
      } else {
        List<Object> list = new ArrayList<>();
        list.add(obj);
        hashStack.put(key, list);
        return false;
      }
    }

    /**
     *
     * @param fullName
     * @param unLoadProperties
     * @param reg
     * @return
     */
    private static boolean judgeUnLoadProperty(String fullName, Set<String> unLoadProperties) {
      if (MyBagUtils.isEmpty(unLoadProperties)) {
        return false;
      }
      for (String regStr : unLoadProperties) {
        if (Pattern.matches(regStr, fullName)) {
          return true;
        }
      }
      return false;
    }

  }
}

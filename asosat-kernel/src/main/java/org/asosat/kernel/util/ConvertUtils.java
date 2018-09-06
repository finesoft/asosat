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
package org.asosat.kernel.util;

import static org.asosat.kernel.util.MyClsUtils.isSimpleClass;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
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
import org.apache.commons.lang3.StringUtils;
import org.asosat.kernel.context.DefaultSetting;

/**
 * @author bingo 上午12:29:05
 *
 */
public class ConvertUtils {

  public final static Set<String> BOOLEAN_TRUE_STRS =
      new HashSet<>(Arrays.asList("1", "t", "true", "y", "yes", "是", "对"));

  public static BigDecimal toBigDecimal(Object obj) {
    return toBigDecimal(obj, null);
  }

  public static BigDecimal toBigDecimal(Object obj, BigDecimal dfltVal) {
    if (obj instanceof BigDecimal) {
      return (BigDecimal) obj;
    } else {
      BigDecimal casted = dfltVal;
      if (obj != null) {
        try {
          casted = new BigDecimal(obj.toString());
        } catch (NumberFormatException e) {
          throw new RuntimeException(e);
        }
      }
      return casted;
    }
  }

  public static List<BigDecimal> toBigDecimalList(Object obj) {
    return toList(obj, ConvertUtils::toBigDecimal);
  }

  public static BigInteger toBigInteger(Object obj) {
    return toBigInteger(obj, null);
  }

  public static BigInteger toBigInteger(Object obj, BigInteger dfltVal) {
    if (obj instanceof BigInteger) {
      return (BigInteger) obj;
    } else {
      BigInteger casted = dfltVal;
      if (obj != null) {
        try {
          casted = new BigInteger(obj.toString());
        } catch (NumberFormatException e) {
          throw new RuntimeException(e);
        }
      }
      return casted;
    }
  }

  public static List<BigInteger> toBigIntegerList(Object obj) {
    return toList(obj, ConvertUtils::toBigInteger);
  }

  public static Boolean toBoolean(Object obj) {
    if (obj instanceof Boolean) {
      return (Boolean) obj;
    } else if (obj instanceof Number) {
      return ((Number) obj).intValue() > 0 ? Boolean.TRUE : Boolean.FALSE;
    } else if (obj != null) {
      return BOOLEAN_TRUE_STRS.contains(obj.toString().trim().toLowerCase(DefaultSetting.LOCALE))
          ? Boolean.TRUE
          : Boolean.FALSE;
    } else {
      return Boolean.FALSE;
    }
  }


  public static Character toCharacter(Object obj) {
    if (obj == null) {
      return null;
    } else if (obj instanceof Character) {
      return (Character) obj;
    } else {
      return Character.valueOf(obj.toString().charAt(0));
    }
  }

  public static Currency toCurrency(Object obj) {
    return toCurrency(obj, null);
  }

  public static Currency toCurrency(Object obj, Currency dfltVal) {
    if (obj == null) {
      return dfltVal;
    } else if (obj instanceof Currency) {
      return (Currency) obj;
    } else {
      return Currency.getInstance(obj.toString());
    }
  }

  public static Double toDouble(Object obj) {
    return toDouble(obj, null);
  }

  public static Double toDouble(Object obj, Double dfltVal) {
    if (obj instanceof byte[]) {
      return EncryptUtils.toDouble((byte[]) obj);
    } else {
      Number num = toNumber(obj);
      if (num == null) {
        return dfltVal;
      } else if (num instanceof Double) {
        return (Double) num;
      }
      return Double.valueOf(num.doubleValue());
    }
  }

  public static List<Double> toDoubleList(Object obj) {
    return toList(obj, ConvertUtils::toDouble);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Enum<T>> T toEnum(Object obj, Class<T> enumClazz) {
    if (obj instanceof Enum<?> && obj.getClass().isAssignableFrom(enumClazz)) {
      return (T) obj;
    } else if (obj != null) {
      String str = obj.toString();
      if (str.chars().allMatch(p -> Character.isDigit(p))) {
        return enumClazz.getEnumConstants()[Integer.valueOf(str).intValue()];
      } else {
        return Enum.valueOf(enumClazz, str);
      }
    }
    return null;
  }

  public static <T extends Enum<T>> List<T> toEnumList(Object obj, Class<T> enumClazz) {
    List<T> list = toList(obj, (e) -> ConvertUtils.toEnum(e, enumClazz));
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
      List<String> tpls = tpl.stream().filter(p -> p.length() > 0).map(m -> m.toString())
          .collect(Collectors.toList());
      tpl.clear();
      if (tpls.stream()
          .anyMatch(p -> p.chars().anyMatch(m -> Character.isAlphabetic(m) || m == '_'))) {
        list = tpls.stream().map(m -> Enum.valueOf(enumClazz, m)).collect(Collectors.toList());
      } else {
        list = tpls.stream().map(m -> enumClazz.getEnumConstants()[Integer.valueOf(m).intValue()])
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
    if (obj instanceof byte[]) {
      return EncryptUtils.toFloat((byte[]) obj);
    } else {
      Number num = toNumber(obj);
      if (num == null) {
        return dfltVal;
      } else if (num instanceof Float) {
        return (Float) num;
      }
      return Float.valueOf(num.floatValue());
    }
  }

  public static List<Float> toFloatList(Object obj) {
    return toList(obj, ConvertUtils::toFloat);
  }

  public static Instant toInstant(Object obj) {
    return toInstant(obj, null);
  }

  public static Instant toInstant(Object obj, Instant dfltVal) {
    if (obj instanceof Instant) {
      return (Instant) obj;
    } else if (obj instanceof Long) {
      return Instant.ofEpochMilli((Long) obj);
    } else if (obj instanceof Temporal) {
      return Instant.from((Temporal) obj);
    } else if (obj instanceof java.util.Date) {
      return Instant.ofEpochMilli(((java.util.Date) obj).getTime());
    } else if (obj != null) {
      return Instant.parse(obj.toString());
    } else {
      return dfltVal;
    }
  }

  public static List<Instant> toInstantList(Object obj) {
    return toList(obj, ConvertUtils::toInstant);
  }

  public static Integer toInteger(Object obj) {
    return toInteger(obj, null);
  }

  public static Integer toInteger(Object obj, Integer dfltVal) {
    if (obj instanceof byte[]) {
      return EncryptUtils.toInt((byte[]) obj);
    } else {
      Number num = toNumber(obj);
      if (num == null) {
        return dfltVal;
      } else if (num instanceof Integer) {
        return (Integer) num;
      }
      return Integer.valueOf(num.intValue());
    }
  }

  public static List<Integer> toIntegerList(Object obj) {
    return toList(obj, ConvertUtils::toInteger);
  }

  public static <T> List<T> toList(Object obj, Function<Object, T> f) {
    List<T> values = new ArrayList<>();
    if (obj instanceof Collection<?>) {
      values = ((Collection<?>) obj).stream().map(x -> f.apply(x)).collect(Collectors.toList());
    } else if (obj instanceof Object[]) {
      values = Arrays.stream((Object[]) obj).map(x -> f.apply(x)).collect(Collectors.toList());
    }
    return values;
  }

  public static LocalDate toLocalDate(Object obj) {
    return toLocalDate(obj, null);
  }

  public static LocalDate toLocalDate(Object obj, LocalDate dfltVal) {
    if (obj instanceof LocalDate) {
      return (LocalDate) obj;
    } else if (obj instanceof List || obj instanceof Object[]) {
      if (MyBagUtils.getSize(obj) >= 3) {
        return LocalDate.of(toInteger(MyBagUtils.get(obj, 0)), toInteger(MyBagUtils.get(obj, 1)),
            toInteger(MyBagUtils.get(obj, 2)));
      } else {
        throw new IllegalArgumentException();
      }
    } else if (obj instanceof java.sql.Date) {
      return ((java.sql.Date) obj).toLocalDate();
    } else if (obj instanceof Long || obj instanceof java.util.Date || obj instanceof Temporal) {
      return toInstant(obj).atZone(ZoneId.systemDefault()).toLocalDate();
    } else if (obj != null) {
      return LocalDate.parse(obj.toString());
    } else {
      return dfltVal;
    }
  }

  public static List<LocalDate> toLocalDateList(Object obj) {
    return toList(obj, ConvertUtils::toLocalDate);
  }

  public static Locale toLocale(Object obj) {
    if (obj == null) {
      return null;
    } else if (obj instanceof Locale) {
      return (Locale) obj;
    } else {
      return Locale.forLanguageTag(obj.toString());
    }
  }

  public static Locale toLocale(Object obj, Locale elseVal) {
    Locale value = toLocale(obj);
    return value != null ? value : elseVal;
  }

  public static Long toLong(Object obj) {
    return toLong(obj, null);
  }

  public static Long toLong(Object obj, Long dfltVal) {
    if (obj instanceof byte[]) {
      return EncryptUtils.toLong((byte[]) obj);
    } else {
      Number num = toNumber(obj);
      if (num == null) {
        return dfltVal;
      } else if (num instanceof Long) {
        return (Long) num;
      }
      return Long.valueOf(num.longValue());
    }
  }

  public static List<Long> toLongList(Object obj) {
    return toList(obj, ConvertUtils::toLong);
  }

  public static Map<?, ?> toMap(Object obj, String... unLoadProperties) {
    return MapConvertor.beanToMap(obj, unLoadProperties);
  }

  public static Number toNumber(Object obj) {
    if (obj == null) {
      return null;
    } else if (obj instanceof Number) {
      return (Number) obj;
    } else {
      try {
        String text = obj.toString();
        return text == null ? null : NumberFormat.getInstance().parse(text);
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T toObject(Object obj) {
    return obj == null ? null : (T) obj;
  }

  public static Short toShort(Object obj) {
    return toShort(obj, null);
  }

  public static Short toShort(Object obj, Short dfltVal) {
    if (obj instanceof byte[]) {
      return EncryptUtils.toShort((byte[]) obj);
    } else {
      Number num = toNumber(obj);
      if (num == null) {
        return dfltVal;
      } else if (num instanceof Short) {
        return (Short) num;
      }
      return Short.valueOf(num.shortValue());
    }
  }

  public static List<Short> toShortList(Object obj) {
    return toList(obj, ConvertUtils::toShort);
  }

  public static String toString(Object obj) {
    return obj == null ? null : obj.toString();
  }

  public static TimeZone toTimeZone(Object obj) {
    if (obj == null) {
      return null;
    } else if (obj instanceof TimeZone) {
      return (TimeZone) obj;
    } else {
      return TimeZone.getTimeZone(obj.toString());
    }
  }

  public static ZonedDateTime toZonedDateTime(Object obj) {
    return toZonedDateTime(obj, null);
  }

  public static ZonedDateTime toZonedDateTime(Object obj, ZonedDateTime dfltVal) {
    if (obj instanceof ZonedDateTime) {
      return (ZonedDateTime) obj;
    } else if (obj instanceof java.sql.Date) {
      return ((java.sql.Date) obj).toLocalDate().atStartOfDay(ZoneId.systemDefault());
    } else if (obj instanceof Long || obj instanceof Temporal || obj instanceof java.util.Date) {
      return toInstant(obj).atZone(ZoneId.systemDefault());
    } else if (obj != null) {
      return ZonedDateTime.parse(obj.toString());
    } else {
      return dfltVal;
    }
  }

  public static List<ZonedDateTime> toZonedDateTimeList(Object obj) {
    return toList(obj, ConvertUtils::toZonedDateTime);
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
        return null;
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
        throw new RuntimeException(e);
      }
      temp.clear();
      temp = null;
      hashStack.clear();
      hashStack = null;
      unLoad.clear();
      unLoad = null;
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
        throw new RuntimeException("Unsupport transform class type : Collection");
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
          throw new RuntimeException(e);
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
        int maxLoopDeep) throws IllegalAccessException, InvocationTargetException,
        NoSuchMethodException, SecurityException {
      List result = new ArrayList<>();
      if (deep == maxLoopDeep) {
        // TODO : 是否加载Id
        return result;
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
        int maxLoopDeep) throws IllegalAccessException, InvocationTargetException,
        NoSuchMethodException, SecurityException {
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
        int maxLoopDeep) throws IllegalAccessException, InvocationTargetException,
        NoSuchMethodException, SecurityException {
      if (obj == null || isSimpleClass(obj.getClass()) || (obj instanceof Map)
          || (obj instanceof Collection) || (obj instanceof Object[])) {
        throw new RuntimeException(
            " Unsupport transform class type :" + (obj == null ? "null" : obj.getClass()));
      }
      if (deep == maxLoopDeep) {
        // TODO : 是否加载Id
        return null;
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
    private static List<Field> getClassAllFields(Class clazz) throws IllegalAccessException,
        InvocationTargetException, NoSuchMethodException, SecurityException {
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

package org.asosat.kernel.normal.conversion;

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.ArrayConverter;
import org.asosat.kernel.normal.conversion.convertor.BigDecimalConvertor;
import org.asosat.kernel.normal.conversion.convertor.BigIntegerConvertor;
import org.asosat.kernel.normal.conversion.convertor.BooleanConvertor;
import org.asosat.kernel.normal.conversion.convertor.ByteConvertor;
import org.asosat.kernel.normal.conversion.convertor.CalendarConvertor;
import org.asosat.kernel.normal.conversion.convertor.CharacterConvertor;
import org.asosat.kernel.normal.conversion.convertor.ClassConvertor;
import org.asosat.kernel.normal.conversion.convertor.CurrencyConvertor;
import org.asosat.kernel.normal.conversion.convertor.DateConvertor;
import org.asosat.kernel.normal.conversion.convertor.DoubleConvertor;
import org.asosat.kernel.normal.conversion.convertor.EnumConvertor;
import org.asosat.kernel.normal.conversion.convertor.FileConvertor;
import org.asosat.kernel.normal.conversion.convertor.FloatConvertor;
import org.asosat.kernel.normal.conversion.convertor.IntegerConvertor;
import org.asosat.kernel.normal.conversion.convertor.LocalDateConvertor;
import org.asosat.kernel.normal.conversion.convertor.LongConvertor;
import org.asosat.kernel.normal.conversion.convertor.ShortConvertor;
import org.asosat.kernel.normal.conversion.convertor.SqlDateConvertor;
import org.asosat.kernel.normal.conversion.convertor.SqlTimeConvertor;
import org.asosat.kernel.normal.conversion.convertor.SqlTimestampConvertor;
import org.asosat.kernel.normal.conversion.convertor.StringConvertor;
import org.asosat.kernel.normal.conversion.convertor.TimeZoneConvertor;
import org.asosat.kernel.normal.conversion.convertor.URLConvertor;
import org.asosat.kernel.normal.conversion.convertor.ZonedDateTimeConvertor;

public class Convertors extends ConvertUtilsBean {

  static Convertor enumConvertor = new EnumConvertor();
  static final Set<Class<?>> supportTypes = new HashSet<>();
  static final Convertors instance = new Convertors(new HashSet<>());

  public static Convertors instance() {
    return instance;
  }

  private Convertors(Set<Class<?>> supportTypes) {
    this.registerPrimitives();
    this.registerStandard();
    this.registerOther();
  }

  @Override
  public String convert(Object value) {
    return super.convert(value);
  }

  @SuppressWarnings("unchecked")
  public <C extends Collection<T>, T> C convertx(Object value, Class<C> collectionType,
      Class<T> targetType) {
    if (value instanceof Collection<?>) {
      if (List.class.isAssignableFrom(collectionType)) {
        return (C) ((Collection<?>) value).stream().map(v -> this.convertx(v, targetType))
            .collect(Collectors.toList());
      } else {
        return (C) ((Collection<?>) value).stream().map(v -> this.convertx(v, targetType))
            .collect(Collectors.toSet());
      }
    } else if (value instanceof Object[]) {
      if (List.class.isAssignableFrom(collectionType)) {
        return (C) Arrays.stream((Object[]) value).map(v -> this.convertx(v, targetType))
            .collect(Collectors.toList());
      } else {
        return (C) Arrays.stream((Object[]) value).map(v -> this.convertx(v, targetType))
            .collect(Collectors.toSet());
      }
    } else {
      if (List.class.isAssignableFrom(collectionType)) {
        return (C) new ArrayList<>();
      } else {
        return (C) new HashSet<>();
      }
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T convertx(Object value, Class<T> targetType) {
    Object obj = super.convert(value, targetType);
    return obj != null ? (T) obj : null;
  }

  @SuppressWarnings("unchecked")
  public <T> T convertx(String value, Class<T> clazz) {
    Object obj = super.convert(value, clazz);
    return obj == null ? null : (T) obj;
  }

  @SuppressWarnings("unchecked")
  public <T> T convertx(String[] values, Class<T> clazz) {
    Object obj = super.convert(values, clazz);
    return obj == null ? null : (T) obj;
  }

  @Override
  public void deregister() {
    super.deregister();
    supportTypes.clear();
    this.registerPrimitives();
    this.registerStandard();
    this.registerOther();
  }

  @Override
  public void deregister(Class<?> clazz) {
    super.deregister(clazz);
    supportTypes.remove(clazz);
  }

  public Set<Class<?>> getSupportTypes() {
    return Collections.unmodifiableSet(supportTypes);
  }

  @Override
  public Converter lookup(Class<?> clazz) {
    Converter converter = super.lookup(clazz);
    if (converter == null && clazz != null) {
      if (clazz.isEnum() || clazz.isArray() && clazz.getComponentType().isEnum()) {
        this.registerx(clazz, enumConvertor);
        if (clazz.isArray()) {
          converter = new ArrayConverter(clazz, enumConvertor, 0);
        } else {
          converter = enumConvertor;
        }
      }
    }
    return converter;
  }

  @Override
  public void register(Converter converter, Class<?> clazz) {
    super.register(converter, clazz);
    supportTypes.add(clazz);
  }

  void registerOther() {

    this.registerx(Class.class, new ClassConvertor());
    this.registerx(java.util.Date.class, new DateConvertor());
    this.registerx(Calendar.class, new CalendarConvertor());
    this.registerx(File.class, new FileConvertor());
    this.registerx(java.sql.Date.class, new SqlDateConvertor());
    this.registerx(java.sql.Time.class, new SqlTimeConvertor());
    this.registerx(Timestamp.class, new SqlTimestampConvertor());
    this.registerx(URL.class, new URLConvertor());

    this.registerx(LocalDate.class, new LocalDateConvertor());
    this.registerx(Currency.class, new CurrencyConvertor());
    this.registerx(TimeZone.class, new TimeZoneConvertor());
    this.registerx(ZonedDateTime.class, new ZonedDateTimeConvertor());
  }

  void registerPrimitives() {
    this.registerx(Boolean.TYPE, new BooleanConvertor());
    this.registerx(Byte.TYPE, new ByteConvertor());
    this.registerx(Character.TYPE, new CharacterConvertor());
    this.registerx(Double.TYPE, new DoubleConvertor());
    this.registerx(Float.TYPE, new FloatConvertor());
    this.registerx(Integer.TYPE, new IntegerConvertor());
    this.registerx(Long.TYPE, new LongConvertor());
    this.registerx(Short.TYPE, new ShortConvertor());
  }

  void registerStandard() {
    this.registerx(BigDecimal.class, new BigDecimalConvertor());
    this.registerx(BigInteger.class, new BigIntegerConvertor());
    this.registerx(Boolean.class, new BooleanConvertor());
    this.registerx(Byte.class, new ByteConvertor());
    this.registerx(Character.class, new CharacterConvertor());
    this.registerx(Double.class, new DoubleConvertor());
    this.registerx(Float.class, new FloatConvertor());
    this.registerx(Integer.class, new IntegerConvertor());
    this.registerx(Long.class, new LongConvertor());
    this.registerx(Short.class, new ShortConvertor());
    this.registerx(String.class, new StringConvertor());
  }

  void registerx(final Class<?> clazz, final Converter converter) {
    this.register(converter::convert, clazz);
    final Class<?> arrayType = Array.newInstance(clazz, 0).getClass();
    Converter arrayConverter = new ArrayConverter(arrayType, converter, 0);
    this.register(arrayConverter::convert, arrayType);
  }


}

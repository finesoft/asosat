package org.asosat.ddd.domain.model;

import static org.apache.commons.lang3.reflect.ConstructorUtils.invokeExactConstructor;
import static org.corant.shared.util.Conversions.toLong;
import static org.corant.shared.util.Conversions.toObject;
import static org.corant.shared.util.Empties.isEmpty;
import static org.corant.shared.util.Objects.asString;
import static org.corant.shared.util.Objects.forceCast;
import static org.corant.shared.util.Strings.isNotBlank;
import static org.corant.suites.bundle.GlobalMessageCodes.ERR_OBJ_NON_FUD;
import static org.corant.suites.bundle.GlobalMessageCodes.ERR_PARAM;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import org.corant.suites.bundle.exception.GeneralRuntimeException;
import org.corant.context.Instances;
import org.corant.suites.ddd.model.Entity;
import org.corant.suites.ddd.model.Entity.EntityReference;
import org.corant.suites.ddd.repository.JPARepository;
import org.corant.suites.ddd.repository.JPARepositoryExtension;

@SuppressWarnings("rawtypes")
public interface AggregateReference<T extends AbstractGenericAggregate> extends EntityReference<T> {

  static <A extends AbstractGenericAggregate, T extends AggregateReference<A>> T of(Object param,
      Class<T> cls) {
    if (param == null) {
      return null; // FIXME like c++ reference
    }
    try {
      if (cls != null) {
        if (cls.isAssignableFrom(param.getClass())) {
          return forceCast(param);
        } else if (param instanceof AbstractGenericAggregate) {
          return invokeExactConstructor(cls, new Object[] {param},
              new Class<?>[] {param.getClass()});
        } else {
          Long id = toLong(param);
          if (id != null) {
            return invokeExactConstructor(cls, new Object[] {id}, new Class<?>[] {Long.class});
          }
        }
      }
    } catch (Exception e) {
      throw new GeneralRuntimeException(e, ERR_OBJ_NON_FUD,
          asString(cls).concat(":").concat(asString(param)));
    }
    throw new GeneralRuntimeException(ERR_OBJ_NON_FUD,
        asString(cls).concat(":").concat(asString(param)));
  }

  static <X extends Entity> X resolve(Serializable id, Class<X> cls) {
    if (id != null && cls != null) {
      return toObject(id, cls);
    }
    throw new GeneralRuntimeException(ERR_PARAM);
  }

  static <X extends Entity> X resolve(String namedQuery, Class<X> cls, Map<Object, Object> params) {
    if (isNotBlank(namedQuery)) {
      Annotation[] quas = JPARepositoryExtension.resolveQualifiers(cls);
      JPARepository jpar = Instances.resolve(JPARepository.class, quas);
      List<X> list = jpar.select(namedQuery, params);
      if (!isEmpty(list)) {
        if (list.size() > 1) {
          throw new GeneralRuntimeException(ERR_OBJ_NON_FUD);
        }
        return list.get(0);
      }
    }
    throw new GeneralRuntimeException(ERR_PARAM);
  }

  static <X> X resolve(String namedQuery, Class<X> cls, Object... params) {
    if (isNotBlank(namedQuery)) {
      Annotation[] quas = JPARepositoryExtension.resolveQualifiers(cls);
      JPARepository jpar = Instances.resolve(JPARepository.class, quas);
      List<X> list = jpar.select(namedQuery, params);
      if (!isEmpty(list)) {
        if (list.size() > 1) {
          throw new GeneralRuntimeException(ERR_OBJ_NON_FUD);
        }
        return list.get(0);
      }
    }
    throw new GeneralRuntimeException(ERR_PARAM);
  }

  static <X> List<X> resolveList(String namedQuery, Class<X> cls, Object... params) {
    Annotation[] quas = JPARepositoryExtension.resolveQualifiers(cls);
    JPARepository jpar = Instances.resolve(JPARepository.class, quas);
    return jpar.select(namedQuery, params);
  }

  @SuppressWarnings("unchecked")
  @Override
  default T retrieve() {
    Class<T> resolveClass = null;
    Class<?> t = getClass();
    do {
      if (t.getGenericSuperclass() instanceof ParameterizedType) {
        resolveClass =
            (Class<T>) ((ParameterizedType) t.getGenericSuperclass()).getActualTypeArguments()[0];
        break;
      } else {
        Type[] genericInterfaces = t.getGenericInterfaces();
        if (genericInterfaces != null) {
          for (Type type : genericInterfaces) {
            if (type instanceof ParameterizedType) {
              ParameterizedType parameterizedType = (ParameterizedType) type;
              if (parameterizedType.getRawType() == AggregateReference.class) {
                resolveClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];
                break;
              }
            }
          }
        }
      }
    } while (resolveClass == null && (t = t.getSuperclass()) != null);
    return resolve(getId(), resolveClass);
  }
}

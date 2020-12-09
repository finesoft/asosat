package org.asosat.ddd.domain.model;

import static org.apache.commons.lang3.reflect.ConstructorUtils.invokeExactConstructor;
import static org.corant.shared.util.Assertions.shouldNotNull;
import static org.corant.shared.util.Conversions.toLong;
import static org.corant.shared.util.Empties.isEmpty;
import static org.corant.shared.util.Objects.asString;
import static org.corant.shared.util.Objects.forceCast;
import static org.corant.shared.util.Strings.isNotBlank;
import static org.corant.suites.bundle.GlobalMessageCodes.ERR_OBJ_NON_FUD;
import static org.corant.suites.bundle.GlobalMessageCodes.ERR_PARAM;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.corant.context.Instances;
import org.corant.suites.bundle.exception.GeneralRuntimeException;
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

  static <X extends Entity> X resolve(Class<X> cls, Serializable id) {
    if (id != null && cls != null) {
      return shouldNotNull(resolveRepository(cls).get(cls, id),
          () -> new GeneralRuntimeException(ERR_PARAM));
    }
    throw new GeneralRuntimeException(ERR_PARAM);
  }

  static <X extends Entity> X resolve(Class<X> cls, String namedQuery, Map<Object, Object> params) {
    if (isNotBlank(namedQuery)) {
      List<X> list = resolveRepository(cls).namedQuery(namedQuery).parameters(params).select();
      if (!isEmpty(list)) {
        if (list.size() > 1) {
          throw new GeneralRuntimeException(ERR_OBJ_NON_FUD);
        }
        return list.get(0);
      }
    }
    throw new GeneralRuntimeException(ERR_PARAM);
  }

  static <X> X resolve(Class<X> cls, String namedQuery, Object... params) {
    if (isNotBlank(namedQuery)) {
      List<X> list = resolveRepository(cls).namedQuery(namedQuery).parameters(params).select();
      if (!isEmpty(list)) {
        if (list.size() > 1) {
          throw new GeneralRuntimeException(ERR_OBJ_NON_FUD);
        }
        return list.get(0);
      }
    }
    throw new GeneralRuntimeException(ERR_PARAM);
  }

  static <X> List<X> resolveList(Class<X> cls, String namedQuery, Object... params) {
    return resolveRepository(cls).namedQuery(namedQuery).parameters(params).select();
  }

  static JPARepository resolveRepository(Class<?> cls) {
    return Instances.resolve(JPARepository.class,
        Instances.resolve(JPARepositoryExtension.class).resolveQualifiers(cls));
  }

  @Override
  default T retrieve() {
    return tryRetrieve().orElseThrow(() -> new GeneralRuntimeException(ERR_PARAM));
  }

  @SuppressWarnings("unchecked")
  @Override
  default Optional<T> tryRetrieve() {
    Class<T> resolvedClass = null;
    Class<?> referenceClass = getClass();
    do {
      if (referenceClass.getGenericSuperclass() instanceof ParameterizedType) {
        resolvedClass = (Class<T>) ((ParameterizedType) referenceClass.getGenericSuperclass())
            .getActualTypeArguments()[0];
        break;
      } else {
        Type[] genericInterfaces = referenceClass.getGenericInterfaces();
        if (genericInterfaces != null) {
          for (Type type : genericInterfaces) {
            if (type instanceof ParameterizedType) {
              ParameterizedType parameterizedType = (ParameterizedType) type;
              if (AggregateReference.class
                  .isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
                resolvedClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];
                break;
              }
            }
          }
        }
      }
    } while (resolvedClass == null && (referenceClass = referenceClass.getSuperclass()) != null);
    if (resolvedClass != null && getId() != null) {
      return Optional.ofNullable(resolveRepository(resolvedClass).get(resolvedClass, getId()));
    }
    return Optional.empty();
  }
}

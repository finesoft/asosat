package org.corant.asosat.ddd.domain.model;

import org.corant.Corant;
import org.corant.kernel.exception.GeneralRuntimeException;
import org.corant.suites.ddd.model.Entity.EntityReference;
import org.corant.suites.ddd.model.EntityLifecycleManager;
import org.corant.suites.ddd.repository.JPARepository;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.corant.kernel.util.Instances.resolveApply;
import static org.corant.shared.util.Empties.isEmpty;
import static org.corant.shared.util.StringUtils.isNotBlank;
import static org.corant.suites.bundle.GlobalMessageCodes.ERR_OBJ_NON_FUD;
import static org.corant.suites.bundle.GlobalMessageCodes.ERR_PARAM;


public interface AggregationReference<T extends AbstractGenericAggregation> extends EntityReference<T> {

    static JPARepository obtainRepo(Annotation... qualifiers) {
        return Corant.instance().select(JPARepository.class, qualifiers).get();
    }

    static <T> T retrieve(Serializable id, Class<T> cls) {
        if (id != null && cls != null) {
            return obtainRepo(resolveApply(EntityLifecycleManager.class, b -> b.persistenceQualifiers(cls))).get(cls, id);
        }
        throw new GeneralRuntimeException(ERR_PARAM);
    }

    static <T> T retrieve(String namedQuery, Annotation[] qualifiers, Object... params) {
        if (isNotBlank(namedQuery)) {
            List<T> persistObjects = obtainRepo(qualifiers).select(namedQuery, params);
            if (!isEmpty(persistObjects)) {
                if (persistObjects.size() > 1) {
                    throw new GeneralRuntimeException(ERR_OBJ_NON_FUD);
                }
                return persistObjects.get(0);
            }
        }
        throw new GeneralRuntimeException(ERR_PARAM);
    }

    static <T> T retrieve(String namedQuery, Map<Object, Object> params, Annotation... qualifiers) {
        if (isNotBlank(namedQuery)) {
            List<T> persistObjects = obtainRepo(qualifiers).select(namedQuery, params);
            if (!isEmpty(persistObjects)) {
                if (persistObjects.size() > 1) {
                    throw new GeneralRuntimeException(ERR_OBJ_NON_FUD);
                }
                return persistObjects.get(0);
            }
        }
        throw new GeneralRuntimeException(ERR_PARAM);
    }

    @SuppressWarnings("unchecked")
    @Override
    default T retrieve() {
        Class<T> resolveClass = null;
        Class<?> t = this.getClass();
        do {
            Type[] genericInterfaces = t.getGenericInterfaces();
            if (genericInterfaces != null) {
                for (Type type : genericInterfaces) {
                    if (type instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = ((ParameterizedType) type);
                        if (parameterizedType.getRawType() == AggregationReference.class) {
                            resolveClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];
                            break;
                        }
                    }
                }
            }
        } while (resolveClass == null && (t = t.getSuperclass()) != null);
        return retrieve(getId(), resolveClass);
    }
}

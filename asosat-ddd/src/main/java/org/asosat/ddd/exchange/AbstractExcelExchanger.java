package org.asosat.ddd.exchange;

import java.lang.reflect.ParameterizedType;
import java.util.logging.Logger;

/**
 * @author don
 * @date 2020-01-10
 */
public class AbstractExcelExchanger<V> {

  private Class<V> resolveClass;

  protected final Logger logger = Logger.getLogger(getClass().getName());

  @SuppressWarnings("unchecked")
  protected Class<V> determineVOClass() {

    if (resolveClass == null) {
      synchronized (getClass()) {
        if (resolveClass == null) {
          Class<?> t = getClass();
          do {
            if (t.getGenericSuperclass() instanceof ParameterizedType) {
              resolveClass = (Class<V>) ((ParameterizedType) t.getGenericSuperclass()).getActualTypeArguments()[0];
              break;
            }
          } while (resolveClass == null && (t = t.getSuperclass()) != null);
        }
      }
    }
    return resolveClass;
  }
}

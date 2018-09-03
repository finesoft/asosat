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
package org.asosat.domain.message;

import static org.asosat.kernel.util.MyClsUtils.getClassPathPackageClassNames;
import static org.asosat.kernel.util.MyClsUtils.tryToLoadClassForName;
import static org.asosat.kernel.util.Preconditions.requireNotEmpty;
import static org.asosat.kernel.util.Preconditions.requireNull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.asosat.kernel.exception.GeneralRuntimeException;
import org.asosat.kernel.resource.ConfigResource;
import org.asosat.domain.annotation.stereotype.InfrastructureServices;
import org.asosat.domain.repository.JpaUtils;

/**
 * @author bingo 下午3:28:14
 *
 */
@ApplicationScoped
@InfrastructureServices
public abstract class AbstractGenericMessageConvertor<P, A> implements MessageConvertor {

  public static final String MSG_PKG = "";
  public static final String MSG_QUE_SPT = ";";
  public static final String DFLT_MSG_PKG = "net.asosat;org.asosat";

  @Inject
  protected ConfigResource configResource;

  protected final Map<String, Constructor<AbstractGenericMessage<P, A>>> constructors =
      new HashMap<>();

  public AbstractGenericMessageConvertor() {}

  @Override
  public AbstractGenericMessage<P, A> from(ExchangedMessage message) {
    if (message != null) {
      Constructor<AbstractGenericMessage<P, A>> ctr = this.constructors.get(message.queueName());
      if (ctr != null) {
        try {
          return ctr.newInstance(message);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException e) {
          throw new GeneralRuntimeException(e, PkgMsgCds.ERR_EXMSG_CVT, message.queueName());
        }
      }
    }
    return null;
  }

  @PreDestroy
  protected void destroy() {

  }

  @PostConstruct
  @SuppressWarnings("unchecked")
  protected synchronized void enable() {
    String paths = this.configResource.getValue(MSG_PKG, String.class, DFLT_MSG_PKG);
    for (String path : paths.split(MSG_QUE_SPT)) {
      getClassPathPackageClassNames(path).forEach(clz -> {
        Class<?> cls = tryToLoadClassForName(clz);
        if (JpaUtils.isPersistenceClass(cls)
            && AbstractGenericMessage.class.isAssignableFrom(cls)) {// only support JPA
          Class<AbstractGenericMessage<P, A>> msgCls = (Class<AbstractGenericMessage<P, A>>) cls;
          Constructor<AbstractGenericMessage<P, A>> match = this.findConstructor(msgCls);
          if (match != null) {
            requireNotEmpty(MessageUtils.extractMessageQueues(cls),
                PkgMsgCds.ERR_MSG_CFG_QUEUE_NULL, cls.getName()).forEach(queue -> {
                  requireNull(this.constructors.put(queue, match), PkgMsgCds.ERR_MSG_CFG_QUEUE_DUP,
                      queue);
                });
          }
        }
      });
    }
  }

  protected Constructor<AbstractGenericMessage<P, A>> findConstructor(
      Class<AbstractGenericMessage<P, A>> cls) {
    return ConstructorUtils.getMatchingAccessibleConstructor(cls, ExchangedMessage.class);
  }

  protected void uniquenessQueue() {
    // TODO FIXME
  }

}

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
package org.asosat.kernel.abstraction;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

/**
 * @author bingo 下午3:03:49
 *
 */
public interface Registerable<D, C> {

  /**
   * 注册一个消费者
   *
   * @param object
   */
  void register(Class<D> datCls, C consumer);


  /**
   * 注销消费者
   *
   * @param object
   */
  void unregister(Class<D> datCls, C consumer);


  public static abstract class DefaultRegisterable<D, C> implements Registerable<D, C> {

    protected final transient Logger logger = Logger.getLogger(getClass().toString());

    private final ConcurrentMap<Class<D>, Set<C>> registration = new ConcurrentHashMap<>();

    @Override
    public void register(Class<D> datCls, C consumer) {
      this.registration.computeIfAbsent(datCls, (ec) -> new CopyOnWriteArraySet<>()).add(consumer);
      this.logger.fine("register event:" + datCls.getSimpleName() + ",consumers:" + consumer);
    }

    @Override
    public void unregister(Class<D> datCls, C consumer) {
      this.registration.getOrDefault(datCls, new CopyOnWriteArraySet<>()).remove(consumer);
    }

    protected ConcurrentMap<Class<D>, Set<C>> getRegistration() {
      return registration;
    }
  }

  public static abstract class DefaultRegisterableSingle<D, C> implements Registerable<D, C> {

    protected final transient Logger logger = Logger.getLogger(getClass().toString());

    private final ConcurrentMap<Class<D>, C> registration = new ConcurrentHashMap<>();

    @Override
    public void register(Class<D> datCls, C consumer) {
      this.registration.put(datCls, consumer);
      this.logger.fine("register event:" + datCls.getSimpleName() + ",consumers:" + consumer);
    }

    @Override
    public void unregister(Class<D> datCls, C consumer) {
      this.registration.remove(datCls);
    }

    protected ConcurrentMap<Class<D>, C> getRegistration() {
      return registration;
    }
  }

}

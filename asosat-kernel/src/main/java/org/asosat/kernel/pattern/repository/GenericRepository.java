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
package org.asosat.kernel.pattern.repository;

import java.util.List;

/**
 * asosat-kernel
 *
 * @author bingo 上午11:01:50
 *
 */
public interface GenericRepository<T, Q, I> {

  /**
   * 找出单个数据对象
   *
   * @param cls 数据对象类型
   * @param id 数据对象id
   * @return
   */
  T get(I id);

  /**
   * 合并一个数据对象
   *
   * @param obj 数据对象类型
   * @return
   */
  T merge(T obj);

  /**
   * 持久化一个数据对象
   *
   * @param obj 数据对象类型
   * @return
   */
  boolean persist(T obj);

  /**
   * 删除一个数据对象
   *
   * @param obj 数据对象类型
   * @return
   */
  boolean remove(T obj);

  /**
   * 通过查询器找出数据对象列表
   *
   * @param cls 数据对象类型
   * @param q 查询对象
   * @return
   */
  List<T> select(Q q);

}

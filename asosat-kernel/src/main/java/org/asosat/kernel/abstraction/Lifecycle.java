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

/**
 * 有领域生命周期的对象接口，对象的生命周期不包含业务，纯粹的对象生命周期状态<br/>
 * 包含生命周期<br/>
 *
 * @author bingo 2013年4月12日
 * @since 1.0
 */
public enum Lifecycle {
  /**
   * 初始化阶段，初始化状态对象并不满足一致性约束，不会进行持久化，经过诺干时间可能会被GC回收。类似“胚胎”
   */
  INITIAL,
  /**
   * 可用阶段，完全满足一致性约束，可以进行持久化类似“出生到退休间”的活动状态
   */
  ENABLED,
  /**
   * 已经消亡，即将被GC回收 类似“死亡”
   */
  DESTROYED
}

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
package org.asosat.kernel.supertype;

import java.io.Serializable;

/**
 * 值对象，一种简单的对象，具有逻辑上的不变性和值拷贝特征，一个对象只有自己的属性能表明自己即为值对象相反一个对象只能通过标识才能表明自己即为实体。
 * 特性：Immutable，SideEffectFree， 构造方式： 1、通过构造函数一次构造 2、使用Builder模式或Factory“原子”创建
 * 3、如果业务上需要逐步“丰富状态”，则采用如下方式： void synchronized setTarget( Point target ) { Assert.assert(
 * this.target == null ); Assert.assert( target != null ); this.target = target; }
 * 计算hash时必须满足状态完整后，再计算。 值对象引用实体时，由于系统实体的等同判断大部分通过Id值对象,因此如果有引用时需要注意引用实体的等同性判断。
 *
 * @see <a href="http://en.wikipedia.org/wiki/Value_object"> Value Object</a>
 * @see <a href="http://martinfowler.com/eaaCatalog/valueObject.html">MF Value Object</a>
 * @see <a href="http://openjdk.java.net/jeps/169">JEP169</a>
 * @see <a href="http://c2.com/cgi/wiki?ValueObject">ValueObject</a>
 * @author bingo 2013年3月21日
 */
public interface Value extends Serializable {

}

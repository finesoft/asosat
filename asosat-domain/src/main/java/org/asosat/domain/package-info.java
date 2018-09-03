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
/**
 * 基础设施包，含应用程序上下文、事件总线、命令总线
 *
 * <pre>
 *<br>DDD CQRS - Flow Chart
 *
 *<br>  UI              Ctrl              AppLogic              DomainLogic         Repositories      RollbackData
 *<br>  ----            ----              ----------            ----------             ----              -----
 *<br> |    |          |    |            |          |          |          |           |    |            |     |
 *<br> |    |          |    |            | Commander  |          |  Domain  |           |    |            |Event|
 *<br> |    |<--Res--- |Cont| <---Reply--| Bus      | <------- |( AggRoot,| --------- |Repo|            |Store|
 *<br> |    |          |roll|            | (UnitOf  |          | Model,   |           |sito|            |     |
 *<br> |    |          |er  |            | worker,  |          | Ctx,Spec,|           |ry  |             -----
 *<br> |    |---Req--->|    | --Commander->| Commander  | -------> | Factory, | ---->     |    |               |
 *<br> |    |          |    |            | handler) |          | Valid..) |     |     |    |               |
 *<br> |    |          |    |            |          |          |          |     |     |    |               |
 *<br> |    |           ----              ----------            ----------      |      ----                |
 *<br> |    |                                   |                            event     |                  |
 *<br> |    |                                   |                           +---|-------+                  |
 *<br> |    |                                   |                           |   +-----------+              |
 *<br> |    |                                   |        --------------     |       --------|---------     |
 *<br> |    |                                   |       |    Event     |    |      |      Event       |----+
 *<br> | UI |                                   <------ | Handler(saga)|<--event--|      Bus         |
 *<br> |    |                                            --------------     |       ------------------
 *<br> |    |                                                           persistence         |
 *<br> |    |                                                    +----------+             event
 *<br> |    |                                                    |                          |
 *<br> |    |           ----                  ----             ------                    -------        ------
 *<br> |    |          |    |                |    |           |      |                  |       |      |      |
 *<br> |    |          |    |                |    |           |      |                  |       |      |      |
 *<br> |    |<--Res--- |Cont| <---Reply----- |Quer|           | Data |                  | Event | ---> | Other|
 *<br> |    |          |roll|                |ySer|           |      |                  | Handl |      | Servi|
 *<br> |    |          |er  |                |vice| <--SQL--> | Base | <---CAP Trans--- | er    |      | ce(  |
 *<br> |    |---Req--->|    |----Query-----> |    |           |      |                  |       |      | Mail,|
 *<br> |    |          |    |                |    |           |      |                  |       |      | JMS  |
 *<br> |    |          |    |                |    |           |      |                  |       |      | ...) |
 *<br>  ----            ----                  ----             ------                    -------        ------
 * </pre>
 *
 * <br>
 * 图说明： <br>
 * 一、UI：Extjs或html页面、AIR界面、移动客户端界面、Swing界面...。
 *
 * <br>
 * 二、Ctrl：HttpRequest控制器、其它Servlet...；主要作用为权限控制约束，用户命令意图的传达，响应用户意图的UI。
 *
 * <br>
 * 三、AppLogic：上下文应用逻辑 ，针对命令服务主要负责组织协调领域对象执行领域逻辑；针对查询服务主要是执行查询参数设置和执行查询； <br>
 * 是一个操作集合；这一层也是作为与其它系统对接的接口层；实现了用例并与用例对应。
 *
 * <br>
 * 四、DomainLogic：领域逻辑，包含可复用的聚合根、实体、值对象、工厂、场景、规格、领域服务等等，封装了一致性及本质上的问题域的解决方案, <br>
 * 表达业务概念，保证业务规则，存放业务数据和业务状态,领域对象的方法设计必须符合地米特原则。 <br>
 * 地米特原则： <br>
 * 1. 可以调用自己的方法 <br>
 * 2. 参数对象的方法 <br>
 * 3. 创建自己或初始化时涉及到其他对象的方法 <br>
 * 4. 它的直接组件的对象的方法(聚合体内部等) <br>
 * 5. 内部引用的无状态Bean的方法
 *
 * <br>
 * 五、Repositories：一组对象的集合，可以查询重新找回删除等等。
 *
 * <br>
 * 六、RollbackData：用于事件回滚系统恢复。
 *
 * <br>
 * 七、责任关系：Ctrl对用户负责，Command为Ctrl负责，领域对象对Command负责，领域服务、校验器、规格执行器等等Stateless为领域对象负责。
 *
 * <br>
 * 八、领域对象（聚合根）之间的交互：基本采用事件和一些常用的设计模式比如Double dispatch，特殊情况可以用Saga调度协调；聚合根之间的关联只保留ID关系，不做对象引用。 <br>
 * 聚合内的领域对象聚合根与实体、值对象关系：聚合根维护所有聚合内对象的完整性及一致性，所有访问领域内对象的操作（大部分是Command）只能通过聚合根，queryservice除外 <br>
 * queryservice是只读操作。
 *
 * <br>
 * 注意：当前的实现还未完整实现CQRS EventSourcing，目前只支持 Transactional Consistent 不支持 Eventual Consistent
 * <p>
 * 常用命名契约：
 * <table border="1" summary="commonly used prefixes in the ddds">
 * <tr>
 * <th id="h1">Prefix</th>
 * <th id="h2">Method Type</th>
 * <th id="h3">Use</th>
 * </tr>
 * <tr>
 * <td headers="h1"><tt>of</tt></td>
 * <td headers="h2">static factory</td>
 * <td headers="h3">Creates an instance where the factory is primarily validating the input
 * parameters, not converting them.</td>
 * </tr>
 * <tr>
 * <td headers="h1"><tt>from</tt></td>
 * <td headers="h2">static factory</td>
 * <td headers="h3">Converts the input parameters to an instance of the target class, which may
 * involve losing information from the input.</td>
 * </tr>
 * <tr>
 * <td headers="h1"><tt>parse</tt></td>
 * <td headers="h2">static factory</td>
 * <td headers="h3">Parses the input string to produce an instance of the target class.</td>
 * </tr>
 * <tr>
 * <td headers="h1"><tt>format</tt></td>
 * <td headers="h2">instance</td>
 * <td headers="h3">Uses the specified formatter to format the values in the temporal object to
 * produce a string.</td>
 * </tr>
 * <tr>
 * <td headers="h1"><tt>get</tt></td>
 * <td headers="h2">instance</td>
 * <td headers="h3">Returns a part of the state of the target object.</td>
 * </tr>
 * <tr>
 * <td headers="h1"><tt>is</tt></td>
 * <td headers="h2">instance</td>
 * <td headers="h3">Queries the state of the target object.</td>
 * </tr>
 * <tr>
 * <td headers="h1"><tt>with</tt></td>
 * <td headers="h2">instance</td>
 * <td headers="h3">Returns a copy of the target object with one element changed; this is the
 * immutable equivalent to a <tt>set</tt> method on a JavaBean.</td>
 * </tr>
 * <tr>
 * <td headers="h1"><tt>plus</tt></td>
 * <td headers="h2">instance</td>
 * <td headers="h3">Returns a copy of the target object with an amount of time added.</td>
 * </tr>
 * <tr>
 * <td headers="h1"><tt>minus</tt></td>
 * <td headers="h2">instance</td>
 * <td headers="h3">Returns a copy of the target object with an amount of time subtracted.</td>
 * </tr>
 * <tr>
 * <td headers="h1"><tt>to</tt></td>
 * <td headers="h2">instance</td>
 * <td headers="h3">Converts this object to another type.</td>
 * </tr>
 * <tr>
 * <td headers="h1"><tt>at</tt></td>
 * <td headers="h2">instance</td>
 * <td headers="h3">Combines this object with another.</td>
 * </tr>
 * </table>
 * </p>
 * 提供常见的业务模型接口及基础实现类，业务系统领域模型基本继承或实现该模块定义的对象
 *
 * @author bingo 2013年3月15日
 * @since 1.0
 */
package org.asosat.domain;

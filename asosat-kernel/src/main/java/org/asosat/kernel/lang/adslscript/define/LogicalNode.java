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
package org.asosat.kernel.lang.adslscript.define;

import java.util.Iterator;
import java.util.List;
import org.asosat.kernel.lang.adslscript.define.Operator.LogicalOperator;

/**
 * asosat-query
 *
 * @author bingo 上午11:42:26
 *
 */
public abstract class LogicalNode implements IterableNode {

  private final List<Node> childs;

  private final LogicalOperator operator;

  /**
   * @param childs
   * @param operator
   */
  LogicalNode(List<Node> childs, LogicalOperator operator) {
    super();
    this.childs = childs;
    this.operator = operator;
  }

  @Override
  public <R, P> R apply(Visitor<R, P> visitor, P param) {
    return visitor.visit(this, param);
  }

  /**
   * @return the childs
   */
  public List<Node> getChilds() {
    return this.childs;
  }

  /**
   * @return the operator
   */
  public LogicalOperator getOperator() {
    return this.operator;
  }

  @Override
  public Iterator<Node> iterator() {
    return this.childs.iterator();
  }

}

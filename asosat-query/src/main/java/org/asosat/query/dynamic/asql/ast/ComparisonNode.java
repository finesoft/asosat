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
package org.asosat.query.dynamic.asql.ast;

import org.asosat.query.dynamic.asql.ast.Operator.ComparisonOperator;

/**
 * asosat-query
 *
 * @author bingo 下午2:39:22
 *
 */
public class ComparisonNode implements Node {

  private final ComparisonOperator operator;

  private final String comparable;

  private final Object candidate;

  /**
   * @param operator
   * @param comparable
   */
  public ComparisonNode(ComparisonOperator operator, String comparable, Object candidate) {
    super();
    this.operator = operator;
    this.comparable = comparable;
    this.candidate = candidate;
  }

  @Override
  public <R, P> R apply(Visitor<R, P> visitor, P param) {
    return visitor.visit(this, param);
  }

  /**
   * @return the candidate
   */
  public Object getCandidate() {
    return this.candidate;
  }

  /**
   * @return the comparable
   */
  public String getComparable() {
    return this.comparable;
  }

  /**
   * @return the operator
   */
  public ComparisonOperator getOperator() {
    return this.operator;
  }

}

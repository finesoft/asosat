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

import java.util.List;
import org.asosat.query.dynamic.asql.ast.Operator.LogicalOperator;

/**
 * asosat-query
 *
 * @author bingo 下午2:07:28
 *
 */
public class AndNode extends LogicalNode {

  /**
   * @param childs
   */
  AndNode(List<Node> childs) {
    super(childs, LogicalOperator.AND);
  }

}

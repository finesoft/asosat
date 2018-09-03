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

import java.util.Collection;

/**
 * 树结构接口 <br>
 *
 * @author bingo 2013年4月12日
 */
public interface TreeNode {

  public static final int FIRST_LEVEL = 1;
  public static final String FIRST_TREE_PATH = "";
  public static final String TREE_PATHINFO_SEPARATOR = ";";

  /**
   * 子对象
   *
   * @return
   */
  Collection<? extends TreeNode> getChilds();

  /**
   * 父对象
   *
   * @return
   */
  TreeNode getParent();

  /**
   * 获取树路径深度
   *
   * @return
   */
  int getPathDeep();

  /**
   * 树路径索引，一般情况下是主键的索引
   *
   * @return
   */
  String getPathIndex();

  /**
   * 相邻
   *
   * @return
   */
  Collection<? extends TreeNode> getSiblings();
}

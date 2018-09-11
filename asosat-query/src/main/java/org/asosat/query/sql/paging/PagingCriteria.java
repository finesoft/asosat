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
package org.asosat.query.sql.paging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * asosat-query
 *
 * @author bingo 上午10:33:04
 *
 */
public class PagingCriteria {

  public static final int SIZE = 16;

  final int start;
  final int size;
  final int currentPage;
  final List<SortField> sortFields = new ArrayList<>();

  public PagingCriteria(int start, int size, int currentPage) {
    this(start, size, currentPage, null);
  }

  /**
   * @param start
   * @param size
   * @param currentPage
   */
  public PagingCriteria(int start, int size, int currentPage, List<SortField> sortFields) {
    super();
    this.start = start;
    this.size = size;
    this.currentPage = currentPage;
    if (sortFields != null) {
      this.sortFields.addAll(sortFields);
    }
  }


  /**
   * @return the currentPage
   */
  public int getCurrentPage() {
    return this.currentPage;
  }

  /**
   * @return the size
   */
  public int getSize() {
    return this.size;
  }

  /**
   * @return the sortFields
   */
  public List<SortField> getSortFields() {
    return Collections.unmodifiableList(this.sortFields);
  }

  /**
   * @return the start
   */
  public int getStart() {
    return this.start;
  }



  public static class SortField {
    final String name;
    final SortOrder order;

    /**
     * @param name
     * @param order
     */
    public SortField(String name, SortOrder order) {
      super();
      this.name = name;
      this.order = order;
    }

    /**
     * @return the name
     */
    public String getName() {
      return this.name;
    }

    /**
     * @return the order
     */
    public SortOrder getOrder() {
      return this.order;
    }

  }

  public static enum SortOrder {
    ASC, DESC
  }
}

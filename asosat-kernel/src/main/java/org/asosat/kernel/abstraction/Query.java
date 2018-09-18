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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * asosat-kernel
 *
 * @author bingo 上午11:57:14
 *
 */
public interface Query<Q, P> {

  <T> T get(Q q, P param);

  default <R, T> T get(Q q, P param, BiFunction<R, Query<Q, P>, T> func) {
    R t = get(q, param);
    return func.apply(t, this);
  }

  <T> PagedList<T> page(Q q, P param);

  default <R, T> PagedList<T> page(Q q, P param, BiFunction<R, Query<Q, P>, T> func) {
    PagedList<R> raw = page(q, param);
    if (raw == null) {
      return PagedList.inst();
    } else {
      PagedList<T> result = new PagedList<>();
      return result
          .withResults(
              raw.getResults().stream().map(i -> func.apply(i, this)).collect(Collectors.toList()))
          .withTotal(raw.total);
    }
  }

  <T> ScrolledList<T> scroll(Q q, P param);

  default <R, T> ScrolledList<T> scroll(Q q, P param, BiFunction<R, Query<Q, P>, T> func) {
    ScrolledList<R> raw = scroll(q, param);
    if (raw == null) {
      return ScrolledList.inst();
    } else {
      ScrolledList<T> result = new ScrolledList<>();
      return result
          .withResults(
              raw.getResults().stream().map(i -> func.apply(i, this)).collect(Collectors.toList()))
          .withHasNext(raw.hasNext);
    }
  }

  <T> List<T> select(Q q, P param);

  default <R, T> List<T> select(Q q, P param, BiFunction<R, Query<Q, P>, T> func) {
    List<R> raw = select(q, param);
    if (raw == null) {
      return new ArrayList<>();
    } else {
      return raw.stream().map(i -> func.apply(i, this)).collect(Collectors.toList());
    }
  }

  <T> Stream<T> stream(Q q, P param);

  public static class PagedList<T> {

    private int total;
    private int pageSize;
    private int currentPage;
    private int totalPages;
    private int offset;
    private List<T> results = new ArrayList<>();

    public static <T> PagedList<T> inst() {
      return new PagedList<>();
    }

    public static <T> PagedList<T> of(int total, List<T> results, int offset, int pageSize) {
      PagedList<T> pl = new PagedList<>();
      return pl.withResults(results).withTotal(total).withOffset(offset).withPageSize(pageSize);
    }

    /**
     * @return the currentPage
     */
    public int getCurrentPage() {
      return currentPage;
    }

    public int getOffset() {
      return offset;
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
      return pageSize;
    }

    /**
     * @return the data
     */
    public List<T> getResults() {
      return results;
    }

    /**
     * @return the total
     */
    public int getTotal() {
      return total;
    }

    public int getTotalPages() {
      return totalPages;
    }

    public PagedList<T> withOffset(int offset) {
      this.offset = offset;
      calPages();
      return this;
    }

    public PagedList<T> withPageSize(int pageSize) {
      this.pageSize = pageSize;
      calPages();
      return this;
    }

    public PagedList<T> withResults(List<T> results) {
      this.results.clear();
      if (results != null) {
        this.results.addAll(results);
      }
      return this;
    }

    public PagedList<T> withTotal(int total) {
      this.total = total;
      calPages();
      return this;
    }

    void calPages() {
      if (pageSize > 0 && offset >= 0) {
        if (total > 0) {
          totalPages = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
        }
        int i = offset + 1;
        currentPage = i % pageSize == 0 ? i / pageSize : i / pageSize + 1;
      }
    }

  }

  public static class ScrolledList<T> {

    private boolean hasNext;
    private final List<T> results = new ArrayList<>();

    ScrolledList() {}

    public static <T> ScrolledList<T> inst() {
      return new ScrolledList<>();
    }

    public static <T> ScrolledList<T> of(List<T> results, boolean hasNext) {
      ScrolledList<T> il = new ScrolledList<>();
      return il.withResults(results).withHasNext(hasNext);
    }

    /**
     * @return the data
     */
    public List<T> getResults() {
      return results;
    }

    public boolean hasNext() {
      return false;
    }

    /**
     * @return the hasNext
     */
    public boolean isHasNext() {
      return hasNext;
    }

    public ScrolledList<T> withHasNext(boolean hasNext) {
      this.hasNext = hasNext;
      return this;
    }

    public ScrolledList<T> withResults(List<T> results) {
      this.results.clear();
      if (results != null) {
        this.results.addAll(results);
      }
      return this;
    }

  }
}

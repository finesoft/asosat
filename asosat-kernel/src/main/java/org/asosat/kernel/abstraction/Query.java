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

  <T> IteratedList<T> iterating(Q q, P param);

  default <R, T> IteratedList<T> iterating(Q q, P param, BiFunction<R, Query<Q, P>, T> func) {
    IteratedList<R> raw = iterating(q, param);
    if (raw == null) {
      return IteratedList.inst();
    } else {
      IteratedList<T> result = new IteratedList<>();
      return result
          .withData(
              raw.getData().stream().map(i -> func.apply(i, this)).collect(Collectors.toList()))
          .withHasNext(raw.hasNext);
    }
  }

  <T> PagedList<T> paging(Q q, P param);

  default <R, T> PagedList<T> paging(Q q, P param, BiFunction<R, Query<Q, P>, T> func) {
    PagedList<R> raw = paging(q, param);
    if (raw == null) {
      return PagedList.inst();
    } else {
      PagedList<T> result = new PagedList<>();
      return result
          .withData(
              raw.getData().stream().map(i -> func.apply(i, this)).collect(Collectors.toList()))
          .withTotal(raw.total);
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

  public static class IteratedList<T> {

    private boolean hasNext;
    private final List<T> data = new ArrayList<>();

    IteratedList() {}

    public static <T> IteratedList<T> inst() {
      return new IteratedList<>();
    }

    public static <T> IteratedList<T> of(List<T> data, boolean hasNext) {
      IteratedList<T> il = new IteratedList<>();
      return il.withHasNext(hasNext).withHasNext(hasNext);
    }

    /**
     * @return the data
     */
    public List<T> getData() {
      return data;
    }

    /**
     * @return the hasNext
     */
    public boolean isHasNext() {
      return hasNext;
    }

    public IteratedList<T> withData(List<T> data) {
      this.data.clear();
      if (data != null) {
        this.data.addAll(data);
      }
      return this;
    }

    public IteratedList<T> withHasNext(boolean hasNext) {
      this.hasNext = hasNext;
      return this;
    }

  }

  public static class PagedList<T> {

    private int total;
    private int pageSize;
    private int currentPage;
    private List<T> data = new ArrayList<>();

    public static <T> PagedList<T> inst() {
      return new PagedList<>();
    }

    public static <T> PagedList<T> of(int total, List<T> data) {
      PagedList<T> pl = new PagedList<>();
      return pl.withData(data).withTotal(total);
    }

    /**
     * @return the data
     */
    public List<T> getData() {
      return data;
    }

    /**
     * @return the total
     */
    public int getTotal() {
      return total;
    }

    public PagedList<T> withData(List<T> data) {
      this.data.clear();
      if (data != null) {
        this.data.addAll(data);
      }
      return this;
    }

    public PagedList<T> withTotal(int total) {
      this.total = total;
      return this;
    }

  }
}

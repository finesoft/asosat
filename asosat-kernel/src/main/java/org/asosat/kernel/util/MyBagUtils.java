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
package org.asosat.kernel.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;

/**
 *
 * @author bingo 上午12:31:10
 *
 */
public abstract class MyBagUtils {

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static <T, C extends Collection> C addTo(final C col, T... objects) {
    if (col != null) {
      for (T obj : objects) {
        col.add(obj);
      }
    }
    return col;
  }

  @SafeVarargs
  public static <T> List<T> asList(T... objects) {
    ArrayList<T> list = new ArrayList<>();
    for (T obj : objects) {
      list.add(obj);
    }
    return list;
  }

  @SafeVarargs
  public static <T> Set<T> asSet(T... objects) {
    return new HashSet<>(asList(objects));
  }

  /**
   * Return the indexed value of the supplied object, the supplied object can be
   * Map/Collection/Array/iterator, implement by apache commons.
   *
   * @see CollectionUtils#get(Object, int)
   * @param obj
   * @return bingo 下午3:35:13
   */
  public static Object get(final Object object, final int index) {
    return CollectionUtils.get(object, index);
  }

  /**
   * Return the size of the supplied object, the supplied object can be
   * Map/Collection/Array/iterator, implement by apache commons.
   *
   * @see CollectionUtils#size(Object)
   * @param obj
   * @return bingo 下午3:35:13
   */
  public static int getSize(Object object) {
    return CollectionUtils.size(object);
  }

  /**
   * Return the supplied Collection is null or empty, implement by apache commons.
   *
   * @see CollectionUtils#isEmpty(Collection)
   * @param collection
   * @return bingo 下午3:35:13
   */
  public static boolean isEmpty(Collection<?> collection) {
    return CollectionUtils.isEmpty(collection);
  }

  /**
   * Return the supplied Map is null or empty, implement by apache commons.
   *
   * @see MapUtils#isEmpty(map)
   * @param map
   * @return bingo 下午3:35:13
   */
  public static boolean isEmpty(Map<?, ?> map) {
    return MapUtils.isEmpty(map);
  }

  public static boolean isEmpty(Object[] array) {
    return ArrayUtils.isEmpty(array);
  }

  public static <F, J, T> List<T> mergeList(List<F> from, List<J> join,
      BiFunction<F, J, T> combination, BiPredicate<F, J> condition, boolean ignoreNotMeet) {
    if (ignoreNotMeet) {
      return new ListJoinPlan<F, J, T>().select(combination).from(from).innerJoin(join)
          .on(condition).execute();
    } else {
      return new ListJoinPlan<F, J, T>().select(combination).from(from).join(join).on(condition)
          .execute();
    }
  }

  static class ListJoinPlan<F, J, T> {

    private List<F> from;
    private List<J> joined;
    private BiPredicate<F, J> p;
    private BiFunction<F, J, T> bi;
    private JoinPlanType joinType = JoinPlanType.LEFT_JION;

    public List<T> execute() {
      List<T> result = new ArrayList<>();
      if (this.joinType == JoinPlanType.LEFT_JION) {
        if (isEmpty(this.joined)) {
          result.addAll(
              this.from.stream().map(x -> this.bi.apply(x, null)).collect(Collectors.toList()));
        } else {
          this.from.stream().forEachOrdered(f -> {
            MutableBoolean matched = new MutableBoolean(false);
            this.joined.stream().forEachOrdered(j -> {
              if (this.p.test(f, j)) {
                result.add(this.bi.apply(f, j));
                matched.setTrue();
              }
            });
            if (!matched.isTrue()) {
              result.add(this.bi.apply(f, null));
              matched.setFalse();
            }
          });
        }
      } else if (this.joinType == JoinPlanType.INNER_JOIN) {
        if (!isEmpty(this.joined)) {
          this.from.stream().forEachOrdered(f -> {
            this.joined.stream().forEachOrdered(j -> {
              if (this.p.test(f, j)) {
                result.add(this.bi.apply(f, j));
              }
            });
          });
        }
      } else {

      }
      return result;
    }

    /**
     * 基准列表，类似SQL: SELECT * FROM ?
     *
     * @param from
     * @return
     */
    public ListJoinPlan<F, J, T> from(List<F> from) {
      this.from = from;
      return this;
    }

    public ListJoinPlan<F, J, T> innerJoin(List<J> joined) {
      return this.join(JoinPlanType.INNER_JOIN, joined);
    }

    /**
     * 连接列表，默认左连，类似SQL: SELECT * FROM T LEFT JOIN ?
     *
     * @param joined
     * @return
     */
    public ListJoinPlan<F, J, T> join(List<J> joined) {
      return this.join(JoinPlanType.LEFT_JION, joined);
    }

    /**
     * 连接条件谓语，类似SQL: SELECT * FROM A JOIN B ON ?
     *
     * @param p
     * @return
     */
    public ListJoinPlan<F, J, T> on(BiPredicate<F, J> p) {
      this.p = p;
      return this;
    }

    /**
     * 合并函数，将 from 的list的元素与joined的元素进行合并，类似SQL: SELECT ?
     *
     * @param bi
     * @return
     */
    public ListJoinPlan<F, J, T> select(BiFunction<F, J, T> bi) {
      this.bi = bi;
      return this;
    }

    /**
     * 连接类型和连接列表，类似SQL: SELECT * FROM T ? ?
     *
     * @param joinType 连接类型，这里先支持 LEFT JOIN 和 INNER JION
     * @param joined 连接列表
     * @return
     */
    ListJoinPlan<F, J, T> join(JoinPlanType joinType, List<J> joined) {
      if (joinType != null) {
        this.joinType = joinType;
      }
      this.joined = joined;
      return this;
    }

    static enum JoinPlanType {
      LEFT_JION, INNER_JOIN, CARTESIAN_JOIN;
    }
  }
}

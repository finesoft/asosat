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

import static org.asosat.kernel.util.MyBagUtils.isEmpty;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Spliterators.AbstractSpliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author bingo 2017年4月7日
 */
public class StreamUtils {

  public static final int DFLE_BATCH_SIZE = 64;

  private StreamUtils() {}

  public static <T> Stream<List<T>> batchCollectStream(int forEachBatchSize, Stream<T> source) {
    return new BatchCollectStreams<>(source, forEachBatchSize).stream();
  }

  /**
   * 将列表打散成并行处理流
   *
   * @param list
   * @param threads 线程数量
   * @return parallelStream
   */
  public static <T> Stream<List<T>> parallelStream(List<T> list, int threads) {
    int batch = threads < 1 || isEmpty(list) ? 1 : (list.size() / threads) + 1;
    return partition(list, batch).parallelStream();
  }

  /**
   * 将列表打散成子列表
   *
   * @param list
   * @param size
   * @return partition
   */
  public static <T> List<List<T>> partition(List<T> list, int size) {
    List<List<T>> result = new ArrayList<>();
    if (list != null) {
      final AtomicInteger counter = new AtomicInteger(0);
      list.stream().collect(Collectors.groupingBy(it -> counter.getAndIncrement() / size)).values()
          .forEach(result::add);
    }
    return result;
  }

  public abstract static class AbstractBatchHandlerSpliterator<T> extends AbstractSpliterator<T> {

    private final int batchSize;
    private Consumer<Long> handler;

    protected AbstractBatchHandlerSpliterator(long est, int additionalCharacteristics,
        int forEachBathSize, Consumer<Long> handler) {
      super(est, additionalCharacteristics);
      this.batchSize = forEachBathSize;
      this.handler = handler == null ? t -> {
      } : handler;
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
      long j = 0;
      do {
        if (j % this.batchSize == 0 && j > 0) {
          this.handler.accept(j);
        }
        j++;
      } while (this.tryAdvance(action));
      this.handler.accept(j);
    }

    @Override
    public Comparator<? super T> getComparator() {
      if (this.hasCharacteristics(SORTED)) {
        return null;
      }
      throw new IllegalStateException();
    }

  }



  public static class BatchCollectStreams<T> {
    private final Stream<T> source;
    private final int batchSize;

    public BatchCollectStreams(Stream<T> source, int forEachBathSize) {
      Objects.requireNonNull(source);
      this.source = source;
      this.batchSize = forEachBathSize < 0 ? DFLE_BATCH_SIZE : forEachBathSize;
    }

    public Stream<List<T>> stream() {
      final Iterator<T> sourceIt = this.source.iterator();
      return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<List<T>>() {
        @Override
        public boolean hasNext() {
          return sourceIt.hasNext();
        }

        @Override
        public List<T> next() {
          List<T> tmpList = new ArrayList<>(BatchCollectStreams.this.batchSize);
          int seq = 0;
          while (sourceIt.hasNext()) {
            tmpList.add(sourceIt.next());
            seq++;
            if (seq >= BatchCollectStreams.this.batchSize) {
              break;
            }
          }
          return tmpList;
        }
      }, Spliterator.IMMUTABLE), false);
    }
  }
}

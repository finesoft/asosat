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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import org.asosat.kernel.exception.NotSupportedException;

public interface CompositeIterable<T> {

  /**
   * The next iterator
   *
   * @param node
   * @return
   */
  Iterator<T> getCompositeIterator();

  /**
   * 广度优先组合对象迭代器
   *
   * @author bingo 2013年10月12日
   * @since 1.0
   */
  static final class BreadthCompositeIterator<T extends CompositeIterable<T>> {

    private final CompositeIterable<T> node;

    public BreadthCompositeIterator(CompositeIterable<T> node) {
      this.node = node;
    }

    public Iterator<T> getAllComponentIterator() {
      if (node == null || node.getCompositeIterator() == null) {
        return null;
      }
      return new Iterator<T>() {
        private Queue<Iterator<T>> queue = new LinkedList<>();
        {
          queue.offer(node.getCompositeIterator());
        }

        @Override
        public boolean hasNext() {
          if (queue.isEmpty()) {
            return false;
          } else {
            Iterator<T> it = queue.peek();
            if (it.hasNext()) {
              return true;
            } else {
              queue.poll();
              return hasNext();
            }
          }
        }

        @Override
        public T next() {
          if (hasNext()) {
            Iterator<T> it = queue.peek();
            T next = it.next();
            if (next != null) {
              queue.offer(next.getCompositeIterator());
            }
            return next;
          } else {
            return null;
          }
        }

        @Override
        public void remove() {
          throw new NotSupportedException();
        }
      };
    }
  }

  public static enum CompositeIteratorType {
    DEPTH, BREADTH
  }

  /**
   * 深度优先组合对象迭代器
   *
   * @author bingo 2013年10月12日
   * @since 1.0
   */
  static final class DepthCompositeIterator<T extends CompositeIterable<T>> {

    private final CompositeIterable<T> node;

    public DepthCompositeIterator(CompositeIterable<T> node) {
      this.node = node;
    }

    public Iterator<T> getAllComponentIterator() {
      if (node == null || node.getCompositeIterator() == null) {
        return null;
      }
      return new Iterator<T>() {
        private Stack<Iterator<T>> stack = new Stack<>();
        {
          stack.push(node.getCompositeIterator());
        }

        @Override
        public boolean hasNext() {
          if (stack.isEmpty()) {
            return false;
          } else {
            Iterator<T> it = stack.peek();
            if (it.hasNext()) {
              return true;
            } else {
              stack.pop();
              return hasNext();
            }
          }
        }

        @Override
        public T next() {
          if (hasNext()) {
            Iterator<T> it = stack.peek();
            T next = it.next();
            if (next != null) {
              stack.push(next.getCompositeIterator());
            }
            return next;
          } else {
            return null;
          }
        }

        @Override
        public void remove() {
          throw new NotSupportedException();
        }
      };
    }
  }
}

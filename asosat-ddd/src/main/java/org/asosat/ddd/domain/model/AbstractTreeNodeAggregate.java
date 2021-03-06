/*
 * Copyright (c) 2013-2018, Bingo.Chen (finesoft@gmail.com).
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
package org.asosat.ddd.domain.model;

import static org.corant.shared.util.Empties.isEmpty;
import static org.corant.shared.util.Objects.areEqual;
import static org.corant.shared.util.Objects.forceCast;
import static org.corant.shared.util.Streams.streamOf;
import static org.corant.suites.bundle.Preconditions.requireFalse;
import static org.corant.suites.bundle.Preconditions.requireTrue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import org.asosat.shared.Participator;
import org.asosat.shared.TreeNode;
import org.corant.shared.util.Iterables;

/**
 * corant-asosat-ddd
 *
 * @author bingo 下午1:43:10
 *
 */
@MappedSuperclass
public abstract class AbstractTreeNodeAggregate<P, T extends AbstractTreeNodeAggregate<P, T>>
    extends AbstractMannedAggregate<P, T> implements TreeNode, Iterable<T> {

  private static final long serialVersionUID = -1269961652281852569L;

  private transient List<T> tmpChilds = new ArrayList<>();

  @Column
  private int pathDeep = 1;

  @Column(length = 2048)
  private String pathIndex;

  public AbstractTreeNodeAggregate() {
    super();
  }

  public AbstractTreeNodeAggregate(Participator creator) {
    super(creator);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void destroy(P param, DestroyingHandler<P, T> handler) {
    requireTrue(isEmpty(this.getChilds()), "");
    if (handler != null) {
      handler.preDestroy(param, (T) this);
    }
    super.destroy(false);
  }

  @Transient
  @java.beans.Transient
  @Override
  public List<T> getChilds() {
    List<T> childs = new ArrayList<>();
    if (!isPhantom()) {
      childs.addAll(this.tmpChilds);
      streamOf(this.toReference().obtainChilds()).forEach(childs::add);
    }
    return childs;
  }

  @Transient
  @java.beans.Transient
  @Override
  public abstract T getParent();

  @Transient
  @java.beans.Transient
  public List<T> getPathChilds() {
    List<T> childs = new ArrayList<>();
    if (!isPhantom()) {
      Iterator<T> it = Iterables.depthIterator(forceCast(this));// FIXME
      while (it.hasNext()) {
        T t = it.next();
        childs.add(t);
      }
    }
    return childs;
  }

  @Override
  public int getPathDeep() {
    return this.pathDeep;
  }

  @Override
  public String getPathIndex() {
    return this.pathIndex;
  }

  @Transient
  @java.beans.Transient
  public List<T> getPathParents() {
    List<T> list = new LinkedList<>();
    T parent = this.getParent();
    while (parent != null) {
      list.add(parent);
      parent = parent.getParent();
    }
    return list;
  }

  @Transient
  @java.beans.Transient
  @Override
  public List<T> getSiblings() {
    List<T> siblings = new ArrayList<>();
    if (!isPhantom()) {
      streamOf(this.toReference().obtainSiblings()).filter(c -> !areEqual(c, this))
          .forEach(siblings::add);
    }
    return siblings;
  }

  @Transient
  public boolean isPathChildOf(T obj) {
    return obj == null ? false : obj.isPathParentOf(this);
  }

  @Transient
  public boolean isPathParentOf(AbstractTreeNodeAggregate<P, T> node) {
    return node == null || node.getParent() == null ? false
        : getId() == null ? this.tmpChilds().contains(node)
            : node.getPathIndex().indexOf(getId().toString() + TREE_PATHINFO_SEPARATOR) != -1;
  }

  public boolean isSameRootWith(T other) {
    return other == null || this.getParent() == null || other.getParent() == null ? false
        : areEqual(
            this.getPathIndex().substring(0, this.getPathIndex().indexOf(TREE_PATHINFO_SEPARATOR)),
            this.getPathIndex().substring(0,
                other.getPathIndex().indexOf(TREE_PATHINFO_SEPARATOR)));
  }

  @Override
  public Iterator<T> iterator() {
    return this.getChilds().iterator();
  }

  @SuppressWarnings("unchecked")
  @Override
  public T preserve(P param, PreservingHandler<P, T> handler) {
    if (handler != null) {
      handler.prePreserve(param, (T) this);
    }
    return (T) super.preserve(false);
  }

  @SuppressWarnings("unchecked")
  protected void handleParent(T parent) {
    requireFalse(areEqual(parent, this) || this.isPathParentOf(parent), "");
    this.handlePathInfo(parent);
    if (isPhantom() && parent != null) {
      parent.tmpChilds().add((T) this);
    }
    this.getChilds().forEach(child -> child.handleParent(child.getParent()));
  }

  protected void handlePathInfo(T parent) {
    if (parent != null) {
      this.setPathDeep(parent.getPathDeep() + 1);
      this.setPathIndex(parent.getPathIndex() + parent.getId() + TreeNode.TREE_PATHINFO_SEPARATOR);
    } else {
      this.setPathDeep(TreeNode.FIRST_LEVEL);
      this.setPathIndex(TreeNode.FIRST_TREE_PATH);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  protected T preserve(boolean immediately) {
    super.preserve(immediately);
    T parent = this.getParent();
    if (parent != null) {
      parent.tmpChilds().remove(this);
    }
    return (T) this;
  }

  protected void setPathDeep(int pathDeep) {
    this.pathDeep = pathDeep;
  }

  protected void setPathIndex(String pathIndex) {
    this.pathIndex = pathIndex;
  }

  @Transient
  @java.beans.Transient
  protected List<T> tmpChilds() {
    return this.tmpChilds;
  }

  protected abstract TreeNodeAggregateReference<T> toReference();

  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
  }

  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
  }
}

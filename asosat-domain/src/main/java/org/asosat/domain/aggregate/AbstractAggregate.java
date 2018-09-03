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
package org.asosat.domain.aggregate;

import static org.asosat.domain.aggregate.PkgMsgCds.ERR_AGG_LC;
import static org.asosat.kernel.util.Preconditions.requireFalse;
import java.beans.Transient;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import org.asosat.domain.aggregate.DefaultAggregateListener.LifcyclePhase;
import org.asosat.domain.event.LifecycleEvent;
import org.asosat.kernel.supertype.Event;
import org.asosat.kernel.supertype.Lifecycle;
import org.asosat.kernel.supertype.Message;

/**
 * @author bingo 下午3:25:51
 *
 */
@MappedSuperclass
@EntityListeners(value = {DefaultAggregateListener.class})
public abstract class AbstractAggregate extends AbstractEntity implements Aggregate {

  private static final long serialVersionUID = -9184534676784775644L;

  protected transient volatile Lifecycle lifecycle = Lifecycle.INITIAL;
  protected transient volatile AggregateAssistant assistant;

  @Version
  @Column(name = "evoVerNum")
  private volatile long evoVerNum = 1L;

  public AbstractAggregate() {}

  @Override
  public synchronized List<Message> extractMessages(boolean flush) {
    return this.assistant().extractMessages(flush);
  }

  /**
   * Publish event to bus.
   */
  @Override
  public void fire(Event event, Annotation... qualifiers) {
    this.assistant().fire(event, qualifiers);
  }

  /**
   * Return an aggregate evolutionary version number, this is equivalent to
   * {@link javax.persistence.Version} in JPA
   */
  @Override
  public synchronized Long getEvoVerNum() {
    return this.evoVerNum;
  }

  /**
   * Identifies whether the aggregate has been persisted or deleted.
   * <li>INITIAL: Just created</li>
   * <li>ENABLED: Has been persisted</li>
   * <li>DESTROYED: If already persisted, the representation is removed from the persistence
   * facility; otherwise it is just a token</li>
   */
  @Override
  @Transient
  @javax.persistence.Transient
  public synchronized Lifecycle getLifecycle() {
    return this.lifecycle;
  }

  /**
   * Identifies whether the aggregate has been deleted or just built
   */
  @Override
  @Transient
  @javax.persistence.Transient
  public synchronized Boolean isPhantom() {
    return this.getId() == null || this.lifecycle != Lifecycle.ENABLED;
  }

  @Override
  public void raise(Message... messages) {
    this.assistant().raise(messages);
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " [id=" + this.getId() + ",evoVerNum = "
        + this.getEvoVerNum() + "]";
  }

  /**
   * Changed the aggregate's property value and return self for lambda use case, Example:
   *
   * <pre>
   * object.with(newXXX, object::setXXX).with(newYYY, object::setYYY)
   * </pre>
   */
  @SuppressWarnings("unchecked")
  public <T extends AbstractAggregate, PT> T with(PT newValue, Consumer<PT> setter) {
    setter.accept(newValue);
    return (T) this;
  }

  /**
   * Obtain an assistant for the aggregate, subclass can rewrite this method for supply an assistant
   */
  protected synchronized AggregateAssistant assistant() {
    if (this.assistant == null) {
      this.assistant = new DefaultAggregateAssistant(this);
    }
    return this.assistant;
  }

  /**
   * Destroy the aggregate if is persisted then remove it from entity manager else just mark
   * destroyed
   */
  protected synchronized void destroy(boolean immediately) {
    this.fire(new LifecycleEvent(this, LifcyclePhase.DESTROY, immediately));
    this.fire(new LifecycleEvent(this, LifcyclePhase.DESTROYED));
  }

  /**
   * Enable the aggregate if is not persisted then persist it else merge it.
   */
  protected synchronized AbstractAggregate enable(boolean immediately) {
    requireFalse(this.getLifecycle() == Lifecycle.DESTROYED, ERR_AGG_LC,
        this.toHumanReader(Locale.getDefault()));
    this.fire(new LifecycleEvent(this, LifcyclePhase.ENABLE, immediately));
    this.fire(new LifecycleEvent(this, LifcyclePhase.ENABLED));
    return this;
  }

  protected synchronized AbstractAggregate initLifecycle(Lifecycle lifecycle) {
    requireFalse(this.getLifecycle() == Lifecycle.DESTROYED, ERR_AGG_LC,
        this.toHumanReader(Locale.getDefault()));
    this.lifecycle = lifecycle;
    return this;
  }

  @Transient
  @javax.persistence.Transient
  protected boolean isPersisted() {
    return this.getId() != null;
  }

  protected synchronized void setEvoVerNum(long evoVerNum) {
    this.evoVerNum = evoVerNum;
  }

}

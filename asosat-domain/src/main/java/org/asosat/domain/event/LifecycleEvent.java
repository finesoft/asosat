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
package org.asosat.domain.event;

import org.asosat.domain.aggregate.LifcyclePhase;
import org.asosat.kernel.abstraction.Aggregate;
import org.asosat.kernel.stereotype.Events;

/**
 * Every aggregate that extends AbstractAggregate when life cycle change then will fire
 * LifecycleEvent, the infrastructure service will listen this event and do persist or remove the
 * aggregate.
 *
 * @author bingo 上午9:39:28
 */
@Events
public class LifecycleEvent extends AbstractEvent {

  private static final long serialVersionUID = -5079236126615952794L;

  private final LifcyclePhase phase;

  private final boolean effectImmediately;

  public LifecycleEvent(Aggregate source, LifcyclePhase lifcyclehase) {
    this(source, lifcyclehase, false);
  }

  public LifecycleEvent(Aggregate source, LifcyclePhase lifcyclehase, boolean effectImmediately) {
    super(source);
    this.phase = lifcyclehase;
    this.effectImmediately = effectImmediately;
  }

  public LifcyclePhase getPhase() {
    return this.phase;
  }

  @Override
  public Aggregate getSource() {
    return super.getSource() == null ? null : (Aggregate) super.getSource();
  }

  public boolean isEffectImmediately() {
    return this.effectImmediately;
  }

  @Override
  public String toString() {
    return "LifecycleEvent [phase=" + this.phase + ", effectImmediately=" + this.effectImmediately
        + "]";
  }

}

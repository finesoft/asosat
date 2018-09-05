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
package org.asosat.kernel.domains.infrastructure;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.asosat.kernel.domains.annotation.stereotype.InfrastructureServices;
import org.asosat.kernel.domains.saga.SagaManager;
import org.asosat.kernel.domains.saga.SagaService;

/**
 * asosat-kernel
 *
 * @author bingo 上午1:02:32
 *
 */
@ApplicationScoped
@InfrastructureServices
public class DefaultSagaService implements SagaService {

  @Inject
  @Any
  protected Instance<SagaManager> sagaManagers;

  @Override
  public Stream<SagaManager> getSagaManagers(Annotation... annotations) {
    Instance<SagaManager> inst = this.sagaManagers.select(annotations);
    if (inst.isResolvable()) {
      return inst.stream();
    } else {
      return Stream.empty();
    }
  }

}

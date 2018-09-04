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
package org.asosat.kernel.pattern.interceptor;

import static org.asosat.kernel.pattern.interceptor.PkgMsgCds.ERR_CT_CTX_NULL;
import static org.asosat.kernel.pattern.interceptor.PkgMsgCds.ERR_CT_DFLT;
import static org.asosat.kernel.util.Preconditions.requireNotNull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.asosat.kernel.exception.GeneralRuntimeException;
import org.asosat.kernel.util.MyMethUtils.MethodSignature;

/**
 * @author bingo 下午8:10:35
 *
 */
@Interceptor
@ConcurrencyThrottle
@Priority(Interceptor.Priority.PLATFORM_BEFORE)
public class ConcurrencyThrottleInterceptor {

  static Map<MethodSignature, Semaphore> THROTTLES = new ConcurrentHashMap<>();

  public ConcurrencyThrottleInterceptor() {}

  @AroundInvoke
  public Object concurrencyThrottleInvocation(final InvocationContext ctx) throws Exception {
    ConcurrencyThrottle ann = requireNotNull(ctx, ERR_CT_CTX_NULL).getMethod()
        .getDeclaredAnnotation(ConcurrencyThrottle.class);
    final int max = Integer.max(ann.max(), ConcurrencyThrottle.DFLT_THRON);
    final boolean fair = ann.fair();
    Semaphore counting = ConcurrencyThrottleInterceptor.THROTTLES
        .computeIfAbsent(new MethodSignature(ctx.getMethod()), (k) -> new Semaphore(max, fair));
    try {
      counting.acquire();
      return ctx.proceed();
    } catch (Exception ex) {
      throw new GeneralRuntimeException(ex, ERR_CT_DFLT);
    } finally {
      counting.release();
    }
  }

}

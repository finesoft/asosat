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
package org.asosat.zest.throntail.provider;

import static org.asosat.kernel.util.MyMapUtils.asMap;
import java.util.Locale;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.asosat.kernel.context.DefaultContext;
import org.asosat.kernel.exception.GeneralRuntimeException;
import org.asosat.kernel.exception.GeneralRuntimeExceptionMessager;

/**
 * @author bingo 下午7:14:45
 *
 */
@Provider
public class CtrlExceptionMapper implements ExceptionMapper<Exception> {

  public CtrlExceptionMapper() {}

  @Override
  public Response toResponse(Exception exception) {
    exception.printStackTrace();
    if (exception instanceof GeneralRuntimeException) {
      GeneralRuntimeException ex = (GeneralRuntimeException) exception;
      return Response.ok(asMap("success", false, "message", ex.getLocalizedMessage(), "attributes",
          ex.getAttributes()), MediaType.APPLICATION_JSON).build();
    } else {
      return Response.ok(asMap("success", false, "message",
          DefaultContext.bean(GeneralRuntimeExceptionMessager.class)
              .getUnknowErrorMessage(Locale.getDefault()),
          "cause", asMap("exception:", exception.getClass().getName(), "message",
              exception.getLocalizedMessage())),
          MediaType.APPLICATION_JSON).build();
    }

  }

}

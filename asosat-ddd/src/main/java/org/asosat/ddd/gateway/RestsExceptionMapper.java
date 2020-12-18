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
package org.asosat.ddd.gateway;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.corant.shared.util.Maps.mapOf;
import static org.corant.suites.bundle.MessageResolver.MessageSource.UNKNOW_ERR_CODE;

import java.util.Locale;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.logging.log4j.Logger;
import org.corant.suites.bundle.MessageResolver;
import org.corant.suites.bundle.exception.GeneralRuntimeException;
import org.corant.suites.ddd.annotation.stereotype.ApplicationServices;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * @author bingo 下午7:14:45
 */
@Provider
@ApplicationServices
@ApplicationScoped
public class RestsExceptionMapper implements ExceptionMapper<Exception> {

  final static Locale locale = Locale.CHINA;// FIXME locale singleton request??

  @Inject
  Logger logger;

  @Inject
  MessageResolver messageResolver;

  @Inject
  @ConfigProperty(name = "gateway.exprose-error-cause", defaultValue = "false")
  Boolean exproseErrorCause;

  public RestsExceptionMapper() {}

  @Override
  public Response toResponse(Exception exception) {
    if (exception instanceof WebApplicationException) {
      logger.warn(exception::getMessage, exception);
      return ((WebApplicationException) exception).getResponse();
    }

    Map<String, Object> res;
    if (exception instanceof GeneralRuntimeException) {
      logger.warn(exception::getLocalizedMessage, exception);
      GeneralRuntimeException gre = (GeneralRuntimeException) exception;
      res = mapOf("message", messageResolver.getMessage(locale, gre),
                  "attributes", gre.getAttributes(),
                  "code", gre.getCodes());
    } else {
      logger.error(exception::getMessage, exception);
      res = mapOf("message", messageResolver.getMessage(locale, UNKNOW_ERR_CODE));
      if (exproseErrorCause) {
        res.put("cause", mapOf("exception:", exception.getClass().getName(), "message", exception.getLocalizedMessage()));
      }
    }
    return Response.serverError().type(APPLICATION_JSON).entity(res).build();
  }
}

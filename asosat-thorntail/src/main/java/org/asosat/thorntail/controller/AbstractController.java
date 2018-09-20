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
package org.asosat.thorntail.controller;

import static org.asosat.kernel.util.MyMapUtils.asMap;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.asosat.kernel.context.DefaultContext;
import org.asosat.kernel.pattern.command.Command;

/**
 * asosat-thorntail
 *
 * @author bingo 下午5:51:09
 *
 */
@ApplicationScoped
public abstract class AbstractController {

  protected <T> T issueCommand(Command cmd) {
    return DefaultContext.issueCommand(cmd);
  }

  protected Response ok() {
    return Response.ok(asMap("success", true)).type(MediaType.APPLICATION_JSON).build();
  }

  protected Response ok(Object obj) {
    return Response.ok(asMap("success", true, "data", obj)).type(MediaType.APPLICATION_JSON)
        .build();
  }
}

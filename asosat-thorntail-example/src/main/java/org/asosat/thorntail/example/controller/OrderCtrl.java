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
package org.asosat.thorntail.example.controller;

import static org.asosat.kernel.util.MyMapUtils.asMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.asosat.kernel.context.DefaultContext;
import org.asosat.thorntail.example.command.ConfirmOrder.ConfirmOrderCmd;
import org.asosat.thorntail.example.command.RemoveOrder.RemoveOrderCmd;
import org.asosat.thorntail.example.command.SaveOrder.SaveOrderCmd;

/**
 * asosat-thorntail-example
 *
 * @author bingo 下午2:23:44
 *
 */
@Path("/order")
public class OrderCtrl {

  @Path("/confirm/")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response confirm(ConfirmOrderCmd cmd) {
    DefaultContext.commander().issue(cmd);
    return Response.ok(asMap("success", true)).type(MediaType.APPLICATION_JSON).build();
  }

  @Path("/remove/")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response remove(RemoveOrderCmd cmd) {
    DefaultContext.commander().issue(cmd);
    return Response.ok(asMap("success", true)).type(MediaType.APPLICATION_JSON).build();
  }

  @POST
  @Path("/save/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response save(SaveOrderCmd cmd) {
    Long id = DefaultContext.commander().issue(cmd);
    return Response.ok(asMap("id", id)).type(MediaType.APPLICATION_JSON).build();
  }
}

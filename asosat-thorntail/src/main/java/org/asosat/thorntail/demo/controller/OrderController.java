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
package org.asosat.thorntail.demo.controller;

import static org.asosat.kernel.util.MyMapUtils.asMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.asosat.kernel.context.DefaultContext;
import org.asosat.thorntail.controller.AbstractController;
import org.asosat.thorntail.demo.command.ConfirmOrder.ConfirmOrderCmd;
import org.asosat.thorntail.demo.command.RemoveOrder.RemoveOrderCmd;
import org.asosat.thorntail.demo.command.SaveOrder.SaveOrderCmd;
import org.asosat.thorntail.demo.provider.OrderQuery;

/**
 * asosat-thorntail
 *
 * @author bingo 下午3:20:58
 *
 */
@Path("/order")
public class OrderController extends AbstractController {

  @Inject
  OrderQuery query;

  @POST
  @Path("/confirm/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response confirm(ConfirmOrderCmd cmd) {
    DefaultContext.commander().issue(cmd);
    return Response.ok(asMap("success", true)).type(MediaType.APPLICATION_JSON).build();
  }

  @POST
  @Path("/query/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response query(Map<String, Object> cmd) {
    List<Map<String, Object>> resultList = this.query.select("Order.select", cmd);
    return Response.ok(resultList).type(MediaType.APPLICATION_JSON).build();
  }

  @POST
  @Path("/remove/")
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

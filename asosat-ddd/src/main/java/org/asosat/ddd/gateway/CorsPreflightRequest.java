package org.asosat.ddd.gateway;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * @author don
 * @date 2020-05-28
 */
@Path("")
@ApplicationScoped
public class CorsPreflightRequest {

  @Path("/**")
  @OPTIONS
  public Response xxx(@Context HttpServletResponse resp) {
    System.out.println("=====================options test=====================");
    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setHeader("Access-Control-Allow-Headers", "origin, content-type, accept, x-requested-with");
    resp.setHeader("Access-Control-Allow-Credentials", "false");
    resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
    resp.setHeader("Access-Control-Max-Age", "3601");
    return Response.noContent().build();
  }
}

package org.asosat.ddd.gateway;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

/**
 * @author don
 * @date 2020-05-28
 */
@Path("")
@ApplicationScoped
public class CorsPreflightRequest {


  //FIXME DON
  @Path("/**")
  @OPTIONS
  public void xxx(@Context HttpServletResponse resp) {
    System.out.println("=====================options test=====================");
    resp.addHeader("Access-Control-Allow-Origin", "*");
    resp.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    resp.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, x-requested-with");
    resp.addHeader("Access-Control-Max-Age", "3600");
  }
}

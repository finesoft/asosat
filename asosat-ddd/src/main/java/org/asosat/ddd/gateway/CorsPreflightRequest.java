package org.asosat.ddd.gateway;

import java.util.Map;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author don
 * @date 2020-05-28
 */
@Path("")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CorsPreflightRequest {

  @Inject
  protected Logger logger;

  @Path("/**")
  @OPTIONS
  public Response get(Map<?, ?> cmd) {
    logger.fine("cors preflight request");
    return Response.noContent().build();
  }
}

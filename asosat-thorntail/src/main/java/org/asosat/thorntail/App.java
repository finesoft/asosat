package org.asosat.thorntail;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Hello world!
 *
 */
@ApplicationScoped
@ApplicationPath("/app")
public class App extends Application {
  public static void main(String[] args) {
    System.out.println("Hello World!");
  }
}

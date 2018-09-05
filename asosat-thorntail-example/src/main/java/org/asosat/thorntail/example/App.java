package org.asosat.thorntail.example;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Hello world!
 *
 */
@ApplicationPath("/app")
public class App extends Application {
  public static void main(String[] args) {
    System.out.println("Hello World!");
  }
}

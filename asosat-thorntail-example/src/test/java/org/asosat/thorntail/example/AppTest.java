package org.asosat.thorntail.example;

import static io.restassured.RestAssured.given;
import static org.asosat.kernel.util.MyBagUtils.asList;
import static org.asosat.kernel.util.MyMapUtils.asMap;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.core.MediaType;
import org.asosat.thorntail.example.command.ConfirmOrder.ConfirmOrderCmd;
import org.asosat.thorntail.example.command.RemoveOrder.RemoveOrderCmd;
import org.asosat.thorntail.example.command.SaveOrder.SaveOrderCmd;
import org.asosat.thorntail.example.util.HibernateSchemaUtils;
import org.asosat.thorntail.example.util.JsonUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.thorntail.test.ThorntailTestRunner;

/**
 * Unit test for simple App.
 */

@RunWith(ThorntailTestRunner.class)
public class AppTest {

  public static void main(String... strings) {
    HibernateSchemaUtils.stdoutPersistes("org.asosat");
  }

  // @Test
  public void stdoutSchema() {
    HibernateSchemaUtils.stdoutUpdateSchema("examplePu", "org.asosat");
  }


  @Test
  public void testConfirmOrder() {
    Object result = given().body(JsonUtils.serialize(new ConfirmOrderCmd().id(1L)))
        .contentType(MediaType.APPLICATION_JSON).when().post("/app/order/confirm/").getBody()
        .as(Map.class);
    System.out.println(JsonUtils.serialize(result));
  }

  @Test
  public void testCreateOrder() {
    Object result = given()
        .body(JsonUtils.serialize(
            new SaveOrderCmd().buyer("buyer").seller("seller").number(UUID.randomUUID().toString())
                .items(asList(asMap("product", "apple", "qty", 64, "unitPrice", 370.50),
                    asMap("product", "mi", "qty", 128, "unitPrice", 233.75),
                    asMap("product", "huawei", "qty", 256, "unitPrice", 326.00)))))
        .contentType(MediaType.APPLICATION_JSON).when().post("/app/order/save/").getBody()
        .as(Map.class);
    System.out.println(JsonUtils.serialize(result));
  }

  @Test
  public void testRemoveOrder() {
    Object result = given().body(JsonUtils.serialize(new RemoveOrderCmd().id(3L)))
        .contentType(MediaType.APPLICATION_JSON).when().post("/app/order/remove/").getBody()
        .as(Map.class);
    System.out.println(JsonUtils.serialize(result));
  }

}

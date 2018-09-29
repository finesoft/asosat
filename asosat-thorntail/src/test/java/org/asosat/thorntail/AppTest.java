package org.asosat.thorntail;

import static io.restassured.RestAssured.given;
import static org.asosat.kernel.util.MyBagUtils.asList;
import static org.asosat.kernel.util.MyMapUtils.asMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import org.asosat.kernel.abstraction.Lifecycle;
import org.asosat.kernel.concurrent.AsynchronousExecutor;
import org.asosat.kernel.normal.conversion.ConversionService;
import org.asosat.kernel.resource.EnumerationResource;
import org.asosat.thorntail.demo.command.ConfirmOrder.ConfirmOrderCmd;
import org.asosat.thorntail.demo.command.RemoveOrder.RemoveOrderCmd;
import org.asosat.thorntail.demo.command.SaveOrder.SaveOrderCmd;
import org.asosat.thorntail.provider.JsonUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.restassured.mapper.ObjectMapperType;
import io.thorntail.test.ThorntailTestRunner;

/**
 * Unit test for simple App.
 */
@RunWith(ThorntailTestRunner.class)
public class AppTest {

  @Inject
  EnumerationResource er;

  @Inject
  ConversionService cs;

  @Inject
  AsynchronousExecutor ae;

  @Test
  public void testConfirmOrder() {
    Object result =
        given().body(new ConfirmOrderCmd().id(1L)).contentType(MediaType.APPLICATION_JSON).when()
            .post("/app/order/confirm/").getBody().as(Map.class);
    System.out.println(JsonUtils.toJsonStr(result));
  }

  @Test
  public void testConvert() {
    this.cs.listConvert(asList("ENABLED", "DESTROYED"), Lifecycle.class).forEach(
        x -> System.out.println(this.er.getEnumItemLiteral(Lifecycle.class.cast(x), Locale.CHINA)));
    System.out.println(
        this.er.getEnumItemLiteral(this.cs.convert("ENABLED", Lifecycle.class), Locale.CHINA));
    this.ae.runAsync(() -> System.out.println("xxxxxxxxxxxxxxxxxx"));
  }

  @Test
  public void testCreateOrder() {
    Object result = given()
        .body(
            new SaveOrderCmd().buyer("buyer").seller("seller").number(UUID.randomUUID().toString())
                .items(asList(asMap("product", "apple", "qty", 64, "unitPrice", 370.50),
                    asMap("product", "mi", "qty", 128, "unitPrice", 233.75),
                    asMap("product", "huawei", "qty", 256, "unitPrice", 326.00))))
        .contentType(MediaType.APPLICATION_JSON).when().post("/app/order/save/").getBody()
        .as(Map.class, ObjectMapperType.JACKSON_2);
    System.out.println(JsonUtils.toJsonStr(result));
  }

  @Test
  public void testQuery() {
    Object result = given().body(new HashMap<>()).contentType(MediaType.APPLICATION_JSON).when()
        .post("/app/order/query/").getBody().as(Map.class, ObjectMapperType.JACKSON_2);
    System.out.println(JsonUtils.toJsonStr(result, true));
  }

  @Test
  public void testRemoveOrder() {
    Object result =
        given().body(new RemoveOrderCmd().id(3L)).contentType(MediaType.APPLICATION_JSON).when()
            .post("/app/order/remove/").getBody().as(Map.class);
    System.out.println(JsonUtils.toJsonStr(result));
  }
}

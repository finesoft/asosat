package org.corant.asosat.ddd.gateway;

import static org.corant.shared.util.Empties.isEmpty;
import static org.corant.shared.util.ObjectUtils.defaultObject;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.corant.suites.ddd.annotation.stereotype.ApplicationServices;
import org.corant.suites.jaxrs.shared.StreamOutputBuilder;
import org.corant.suites.servlet.abstraction.ContentDispositions;
import org.corant.suites.servlet.abstraction.ContentDispositions.ContentDisposition;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

/**
 * @author bingo 下午5:51:09
 */
@ApplicationScoped
@ApplicationServices
public abstract class AbstractRests {

  protected static final Map<Class<?>, String> cachedPaths = new ConcurrentHashMap<>();

  /**
   * 202
   *
   * @param obj
   * @return accepted
   */
  protected Response accepted() {
    return Response.accepted().build();
  }

  /**
   * 202
   *
   * @param obj
   * @return accepted
   */
  protected Response accepted(Object obj) {
    return Response.accepted(obj).type(MediaType.APPLICATION_JSON).build();
  }

  /**
   * 201
   *
   * @param id
   * @return created
   */
  protected Response created(Object id) {
    return created(URI.create(resolvePath() + "/get/" + id));
  }

  /**
   * 201
   *
   * @param location
   * @return created
   */
  protected Response created(URI location) {
    return Response.created(location).build();
  }

  /**
   * 204
   *
   * @return noContent
   */
  protected Response noContent() {
    return Response.noContent().build();
  }

  /**
   * 200
   *
   * @return ok
   */
  protected Response ok() {
    return Response.ok().type(MediaType.APPLICATION_JSON).build();
  }

  /**
   * 200
   *
   * @param obj
   * @return ok
   */
  protected Response ok(Object obj) {
    return Response.ok(obj).type(MediaType.APPLICATION_JSON).build();
  }

  /**
   * 解析上传文件名
   *
   * @param headers
   * @return parseMpFileName
   */
  protected String parseMpFileName(MultivaluedMap<String, String> headers) {
    ContentDisposition cd = ContentDispositions.parse(headers.getFirst("Content-Disposition"));
    return defaultObject(cd.getFilename(), "unnamed-" + System.currentTimeMillis());
  }

  /**
   * 解析 multipart/form-data 字段
   *
   * @param uploadForm
   * @param fieldNames
   * @return
   * @throws IOException parseMpFormFields
   */
  protected Map<String, Object> parseMpFormFields(Map<String, List<InputPart>> uploadForm,
      String... fieldNames) throws IOException {
    if (isEmpty(uploadForm) || isEmpty(fieldNames)) {
      return new LinkedHashMap<>();
    }
    Map<String, Object> map = new LinkedHashMap<>(uploadForm.size());
    for (String fieldName : fieldNames) {
      List<String> lp = new ArrayList<>();
      if (uploadForm.get(fieldName) != null) {
        for (InputPart ip : uploadForm.get(fieldName)) {
          if (ip != null) {
            lp.add(ip.getBodyAsString());
          }
        }
      }
      if (lp.size() > 1) {
        map.put(fieldName, lp);
      } else if (lp.size() == 1) {
        map.put(fieldName, lp.get(0));
      } else {
        map.put(fieldName, null);
      }
    }
    return map;
  }

  protected String resolvePath() {
    return cachedPaths.computeIfAbsent(getClass(), (cls) -> {
      Annotation[] annotations = cls.getAnnotations();
      for (Annotation annotation : annotations) {
        if (annotation instanceof Path) {
          Path pathAnnotation = (Path) annotation;
          return pathAnnotation.value();
        }
      }
      return "";
    });
  }

  protected StreamOutputBuilder stream(InputStream is) {
    return StreamOutputBuilder.of(is);
  }

}

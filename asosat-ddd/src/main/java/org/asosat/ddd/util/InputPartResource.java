package org.asosat.ddd.util;

import static javax.ws.rs.core.HttpHeaders.CONTENT_DISPOSITION;
import static org.corant.shared.util.Assertions.shouldNotNull;
import static org.corant.shared.util.MapUtils.immutableMapOf;
import static org.corant.shared.util.ObjectUtils.defaultObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;
import org.corant.shared.util.Resources.Resource;
import org.corant.shared.util.Resources.SourceType;
import org.corant.suites.servlet.abstraction.ContentDispositions;
import org.corant.suites.servlet.abstraction.ContentDispositions.ContentDisposition;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

/**
 * resteasy InputPart resource
 *
 * @author don
 * @date 2019-09-26
 */
public class InputPartResource implements Resource {

  private InputPart inputPart;

  private String filename;

  public InputPartResource(InputPart inputPart) {
    this.inputPart = shouldNotNull(inputPart);
    ContentDisposition disposition =
        ContentDispositions.parse(inputPart.getHeaders().getFirst(CONTENT_DISPOSITION));
    filename = defaultObject(disposition.getFilename(), () -> "unnamed-" + UUID.randomUUID());
  }

  @Override
  public String getLocation() {
    return filename;
  }

  @Override
  public Map<String, Object> getMetadata() {
    return immutableMapOf("sourceType", getSourceType(), "fileName", getName(), "lastModified",
        lastModified(), "contentLength", contentLength());
  }

  @Override
  public String getName() {
    return filename;
  }

  @Override
  public SourceType getSourceType() {
    return null;
  }

  public long lastModified() {
    return -1;// FIXME DON
  }

  public long contentLength() {
    return -1;// FIXME DON
  }

  public String getContentType() {
    return inputPart.getMediaType().toString();
  }

  @Override
  public InputStream openStream() throws IOException {
    return inputPart.getBody(InputStream.class, null);
  }
}

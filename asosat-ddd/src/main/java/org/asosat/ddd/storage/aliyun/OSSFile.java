package org.asosat.ddd.storage.aliyun;

import com.aliyun.oss.model.OSSObject;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;
import org.asosat.ddd.storage.StorageFile;
import org.corant.shared.util.Resources.SourceType;

/**
 * @author don
 * @date 2020/12/14
 */
public class OSSFile implements StorageFile {

  static final String FILE_NAME_KEY = "fileName";

  private OSSObject oss;

  public OSSFile(OSSObject oss) {
    this.oss = oss;
  }

  @Override
  public Map<String, Object> getMetadata() {
    Map<String, Object> meta = new TreeMap<>();
    oss.getObjectMetadata().getUserMetadata().forEach(meta::put);
    return meta;
  }

  @Override
  public String getName() {
    return oss.getObjectMetadata().getUserMetadata().get(FILE_NAME_KEY);
  }

  @Override
  public long getCreatedTime() {
    return oss.getObjectMetadata().getLastModified().getTime();
  }

  @Override
  public String getId() {
    return oss.getKey();
  }

  @Override
  public long getLength() {
    return oss.getObjectMetadata().getContentLength();
  }

  @Override
  public String getContentType() {
    return oss.getObjectMetadata().getContentType();
  }

  @Override
  public String getLocation() {
    return getName();
  }

  @Override
  public InputStream openStream() {
    return oss.getObjectContent();
  }

  @Override
  public SourceType getSourceType() {
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T unwrap() {
    return (T) oss;
  }
}

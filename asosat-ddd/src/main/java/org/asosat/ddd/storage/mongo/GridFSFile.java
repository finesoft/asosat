package org.asosat.ddd.storage.mongo;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static org.corant.shared.util.Assertions.shouldNotNull;
import static org.corant.shared.util.Strings.asDefaultString;

import com.mongodb.client.gridfs.GridFSDownloadStream;
import java.io.InputStream;
import java.util.Map;
import org.asosat.ddd.storage.StorageFile;
import org.corant.shared.util.Resources.SourceType;

/**
 * @author don
 * @date 2019-09-27
 */
public class GridFSFile implements StorageFile {

  private final GridFSDownloadStream stream;

  public GridFSFile(GridFSDownloadStream stream) {
    this.stream = shouldNotNull(stream);
  }

  @Override
  public long getCreatedTime() {
    return stream.getGridFSFile().getUploadDate().getTime();
  }

  @Override
  public String getId() {
    return String.valueOf(stream.getGridFSFile().getId().asInt64().longValue());
  }

  @Override
  public long getLength() {
    return stream.getGridFSFile().getLength();
  }

  @Override
  public String getContentType() {
    return asDefaultString(stream.getGridFSFile().getMetadata().get(CONTENT_TYPE));
  }

  @Override
  public String getLocation() {
    return getName();
  }

  @Override
  public Map<String, Object> getMetadata() {
    return stream.getGridFSFile().getMetadata();
  }

  @Override
  public String getName() {
    return stream.getGridFSFile().getFilename();
  }

  @Override
  public InputStream openStream() {
    return stream;
  }

  @Override
  public SourceType getSourceType() {
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T unwrap() {
    return (T) stream;
  }
}

package org.asosat.ddd.storage;

import static org.corant.shared.util.Assertions.shouldNotNull;
import java.io.InputStream;
import java.util.Map;
import org.asosat.ddd.storage.StorageService.StorageFile;
import org.corant.shared.util.Resources.SourceType;
import com.mongodb.client.gridfs.GridFSDownloadStream;

/**
 * @author don
 * @date 2019-09-27
 */
class GridFSStorageFile implements StorageFile {

  private final GridFSDownloadStream stream;

  public GridFSStorageFile(GridFSDownloadStream stream) {
    this.stream = shouldNotNull(stream);
  }

  @Override
  public long getCreatedTime() {
    return stream.getGridFSFile().getUploadDate().getTime();
  }

  @Override
  public Long getId() {
    return stream.getGridFSFile().getId().asInt64().longValue();
  }

  @Override
  public long getLength() {
    return stream.getGridFSFile().getLength();
  }

  @Override
  public String getLocation() {
    return stream.getGridFSFile().getFilename();
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
  public SourceType getSourceType() {
    return null;
  }

  @Override
  public InputStream openStream() {
    return stream;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T unwrap() {
    return (T) stream;
  }
}

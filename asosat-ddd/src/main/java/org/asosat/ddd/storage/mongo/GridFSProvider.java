package org.asosat.ddd.storage.mongo;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static org.corant.shared.util.Assertions.shouldNotBlank;
import static org.corant.shared.util.Assertions.shouldNotNull;
import static org.corant.shared.util.Objects.defaultObject;
import static org.corant.shared.util.Strings.EMPTY;
import static org.corant.suites.mongodb.MongoClientExtension.bsonId;

import com.mongodb.MongoGridFSException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import org.asosat.ddd.storage.FileDeprecatedEvent;
import org.asosat.ddd.storage.StorageFile;
import org.asosat.ddd.storage.StorageService;
import org.bson.Document;
import org.corant.shared.normal.Defaults;
import org.corant.shared.util.FileUtils;

public class GridFSProvider implements StorageService {

  public static final int DFLT_CHUNK_SIZE_BYTES = Defaults.SIXTEEN_KBS * 16;

  protected final Logger logger = Logger.getLogger(getClass().getName());

  protected final GridFSBucket bucket;

  public GridFSProvider(MongoDatabase dataBase, String bucketName) {
    this.bucket = GridFSBuckets.create(dataBase, bucketName);
  }

  @Override
  public String putFile(String idStr,InputStream input, String filename, Map<String, Object> meta) {
    shouldNotNull(input);
    shouldNotBlank(idStr);
    Long id = Long.valueOf(idStr);
    meta = defaultObject(meta, Collections::emptyMap);
    Object contentType = meta.get(CONTENT_TYPE);
    if (contentType == null) {
      contentType = defaultObject(FileUtils.getContentType(filename), EMPTY);
    }
    Document document = new Document(CONTENT_TYPE, contentType);
    for (var key : Arrays.asList(KEY_OWNER_ID, KEY_ORG_ID)) {
      document.put(key, meta.get(key));
    }
    GridFSUploadOptions opt = new GridFSUploadOptions()
        .chunkSizeBytes(DFLT_CHUNK_SIZE_BYTES)
        .metadata(document);
    getBucket().uploadFromStream(bsonId(id), filename, input, opt);
    return id.toString();
  }

  @Override
  public StorageFile getFile(String id) {
    try {
      GridFSDownloadStream stream = getBucket().openDownloadStream(bsonId(Long.valueOf(id)));
      return new GridFSFile(stream);// GridFSDownloadStream 只要不调用read,都可以不关闭
    } catch (MongoGridFSException e) {
      logger.log(Level.WARNING, e, () -> "get file error, id " + id);
      return null;
    }
  }

  @Override
  public void removeFile(String id) {
    getBucket().delete(bsonId(Long.valueOf(id)));
  }

  protected GridFSBucket getBucket() {
    return bucket;
  }

  void onFileDeprecated(@Observes(during = TransactionPhase.AFTER_SUCCESS) FileDeprecatedEvent e) {
    if (e.getUri() != null) {
      removeFile(e.getUri());
    }
  }
}

package org.asosat.ddd.storage.aliyun;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static org.corant.shared.util.Assertions.shouldNotBlank;
import static org.corant.shared.util.Objects.asString;
import static org.corant.shared.util.Objects.defaultObject;
import static org.corant.shared.util.Strings.EMPTY;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.StorageClass;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.asosat.ddd.storage.StorageFile;
import org.asosat.ddd.storage.StorageService;
import org.asosat.ddd.util.GlobalUUIDGenerator;
import org.corant.shared.util.FileUtils;
import org.corant.suites.servlet.abstraction.ContentDispositions.ContentDisposition;

/**
 * @author don
 * @date 2020/12/14
 */
public class OSSProvider implements StorageService {

  protected final OSS ossClient;

  protected final String bucketName;

  public OSSProvider(String endpoint, String accessKeyId, String accessKeySecret, String bucketName) {
    shouldNotBlank(endpoint, "aliyun oss endpoint config error");
    shouldNotBlank(accessKeyId, "aliyun oss accessKeyId config error");
    shouldNotBlank(accessKeySecret, "aliyun oss accessKeySecret config error");
    this.bucketName = shouldNotBlank(bucketName, "aliyun oss defaultBucketName config error");
    this.ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
  }

  @Override
  public StorageFile getFile(String id) {
    OSSObject object = ossClient.getObject(bucketName, id);
    return object != null ? new OSSFile(object) : null;
  }

  @Override
  public String putFile(InputStream input, String filename, Map<String, Object> meta) {
    String id = GlobalUUIDGenerator.generate().toString();
    meta = defaultObject(meta, Collections::emptyMap);
    Object contentType = meta.get(CONTENT_TYPE);
    if (contentType == null) {
      contentType = defaultObject(FileUtils.getContentType(filename), EMPTY);
    }
    ObjectMetadata ossMeta = new ObjectMetadata();
    ossMeta.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
    ossMeta.setContentType(contentType.toString());
    ossMeta.setContentDisposition(new ContentDisposition(null, null, filename, UTF_8, null, null, null, null).toString());
    ossMeta.addUserMetadata(OSSFile.FILE_NAME_KEY, filename);
    for (var key : Arrays.asList(KEY_OWNER_ID, KEY_ORG_ID)) {
      ossMeta.addUserMetadata(key, asString(meta.get(key), null));
    }
    ossClient.putObject(new PutObjectRequest(bucketName, id, input, ossMeta));
    return id;
  }

  @Override
  public void removeFile(String id) {
    ossClient.deleteObject(bucketName, id);
  }

  public void shutdown() {
    ossClient.shutdown();
  }
}

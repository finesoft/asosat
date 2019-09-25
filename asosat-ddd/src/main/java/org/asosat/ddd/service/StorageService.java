/*
 * Copyright (c) 2013-2018, Bingo.Chen (finesoft@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.asosat.ddd.service;

import static org.corant.kernel.util.Instances.resolveAccept;
import static org.corant.kernel.util.Instances.resolveApply;
import static org.corant.kernel.util.Instances.resolveNamed;
import static org.corant.shared.util.Assertions.shouldBeTrue;
import static org.corant.shared.util.Assertions.shouldNotNull;
import static org.corant.shared.util.ConversionUtils.toLong;
import static org.corant.shared.util.StringUtils.contains;
import static org.corant.shared.util.StringUtils.isNoneBlank;
import static org.corant.shared.util.StringUtils.isNotBlank;
import static org.corant.shared.util.StringUtils.left;
import static org.corant.shared.util.StringUtils.right;
import static org.corant.shared.util.StringUtils.trim;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import org.corant.kernel.normal.Names;
import org.corant.shared.exception.CorantRuntimeException;
import org.corant.shared.util.Identifiers;
import org.corant.shared.util.Resources.Resource;
import org.corant.suites.ddd.annotation.stereotype.InfrastructureServices;
import org.corant.suites.ddd.event.AbstractEvent;
import org.corant.suites.mongodb.AbstractGridFSBucketProvider;
import org.corant.suites.mongodb.MongoClientExtension;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;

/**
 * asosat-ddd
 *
 * @author bingo 上午10:36:11
 *
 */
public interface StorageService<S> {

  static void clean(Long id) {
    resolveAccept(GridFSStorageService.class, t -> t.removeFile(shouldNotNull(id)));
  }

  static Long store(Resource resource) {
    try (InputStream is = shouldNotNull(resource).openStream()) {
      return resolveApply(GridFSStorageService.class,
          t -> t.putFile(is, resource.getLocation(), resource.getMetadatas()));
    } catch (IOException e) {
      throw new CorantRuntimeException(e);
    }

  }

  S getFile(Long id);

  Long putFile(InputStream is, String filename, Map<String, Object> metadata);

  void removeFile(Long id);

  /**
   * asosat-ddd
   *
   * @author bingo 下午12:03:06
   *
   */
  public class FileDeprecated extends AbstractEvent {

    private static final long serialVersionUID = -2265993446048435321L;

    private final String uri;

    public FileDeprecated(Object source, String uri) {
      super(source);
      this.uri = uri;
    }

    public String getUri() {
      return uri;
    }

  }

  @ApplicationScoped
  @InfrastructureServices
  public static class GridFSStorageService extends AbstractGridFSBucketProvider
      implements StorageService<GridFSDownloadStream> {

    @Inject
    MongoClientExtension extension;

    @Inject
    @ConfigProperty(name = "storage.gridfs.database-bucket")
    protected Optional<String> qualifier;

    @Inject
    @ConfigProperty(name = "storage.gridfs.identifier.generator.worker-id", defaultValue = "1")
    protected int defaultWorkerId;

    protected GridFSBucket bucket;

    protected MongoDatabase dataBase;

    @Override
    public GridFSBucket getBucket() {
      return bucket;
    }

    @Override
    public GridFSDownloadStream getFile(Long id) {
      return super.getFile(id);
    }

    public Long nextId() {
      return (Long) Identifiers.snowflakeBufferUUIDGenerator(defaultWorkerId, true)
          .generate(() -> extension.getDatabaseLocalTime(dataBase).toEpochMilli());
    }

    @Override
    public Long putFile(InputStream is, String filename, Map<String, Object> metadata) {
      Long id = nextId();
      super.putFile(id, filename, DFLT_CHUNK_SIZE_BYTES, is, metadata);
      return id;
    }

    @Override
    public void removeFile(Long id) {
      super.removeFile(id);
    }

    void onFileDeprecated(@Observes(during = TransactionPhase.AFTER_SUCCESS) FileDeprecated e) {
      if (e.getUri() != null) {
        removeFile(toLong(e.getUri()));
      }
    }

    @PostConstruct
    void onPostConstruct() {
      String dataBaseName = null;
      String bucketName = null;
      String qualifier = this.qualifier.get();
      if (isNotBlank(qualifier) && contains(qualifier, Names.NAME_SPACE_SEPARATORS)) {
        int lastDot = qualifier.lastIndexOf(Names.NAME_SPACE_SEPARATOR);
        dataBaseName = trim(left(qualifier, lastDot));
        bucketName = trim(right(qualifier, qualifier.length() - lastDot - 1));
      }
      shouldBeTrue(isNoneBlank(dataBaseName, bucketName), "GridFSStroageService initialize error, "
          + "please check the value of configuration item['stroage.gridfs.database-bucket'], "
          + "the correct value must contain the database name and bucket name and be connected by '.'");
      final String dn = dataBaseName;
      dataBase = resolveNamed(MongoDatabase.class, dataBaseName).orElseThrow(
          () -> new CorantRuntimeException("Can not find any mongo database by name %s.", dn));
      bucket = GridFSBuckets.create(dataBase, bucketName);
    }
  }
}

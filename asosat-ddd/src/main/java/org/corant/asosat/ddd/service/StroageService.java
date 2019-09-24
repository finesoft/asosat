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
package org.corant.asosat.ddd.service;

import static org.corant.kernel.util.Instances.resolveNamed;
import static org.corant.shared.util.ConversionUtils.toLong;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import org.corant.shared.util.Identifiers;
import org.corant.suites.ddd.event.AbstractEvent;
import org.corant.suites.mongodb.AbstractGridFSBucketProvider;
import org.corant.suites.mongodb.MongoClientExtension;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;

/**
 * asosat-ddd
 *
 * @author bingo 上午10:36:11
 *
 */
public interface StroageService<S> {

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
  public static class GridFSStroageService extends AbstractGridFSBucketProvider
      implements StroageService<GridFSDownloadStream> {

    @Inject
    MongoClientExtension extension;

    @Inject
    @ConfigProperty(name = "stroage.gridfs.bucket-name")
    protected Optional<String> bucketName;

    @Inject
    @ConfigProperty(name = "stroage.gridfs.database-name")
    protected Optional<String> dataBaseName;

    @Inject
    @ConfigProperty(name = "stroage.gridfs.worker-id", defaultValue = "1")
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
      bucket = resolveNamed(GridFSBucket.class, bucketName.orElse("")).get();
      dataBase = resolveNamed(MongoDatabase.class, bucketName.orElse("")).get();
    }
  }
}

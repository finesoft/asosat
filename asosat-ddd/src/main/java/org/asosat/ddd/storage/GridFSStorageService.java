package org.asosat.ddd.storage;

import com.mongodb.MongoGridFSException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import org.corant.kernel.normal.Names;
import org.corant.shared.exception.CorantRuntimeException;
import org.corant.shared.util.Identifiers;
import org.corant.suites.ddd.annotation.stereotype.InfrastructureServices;
import org.corant.suites.mongodb.AbstractGridFSBucketProvider;
import org.corant.suites.mongodb.MongoClientExtension;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import static org.corant.kernel.util.Instances.resolveNamed;
import static org.corant.shared.util.Assertions.shouldBeTrue;
import static org.corant.shared.util.ConversionUtils.toLong;
import static org.corant.shared.util.StringUtils.*;

@ApplicationScoped
@InfrastructureServices
public class GridFSStorageService extends AbstractGridFSBucketProvider implements StorageService {

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
    public StorageFile getFile(Long id) {
        GridFSDownloadStream stream = super.getFile(id);//GridFSDownloadStream 只要不调用read,都可以不关闭
        return new GridFSStorageFile(stream);
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

    void onFileDeprecated(@Observes(during = TransactionPhase.AFTER_SUCCESS) FileDeprecatedEvent e) {
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

    private Long nextId() {
        return (Long) Identifiers.snowflakeBufferUUIDGenerator(defaultWorkerId, true)
                .generate(() -> extension.getDatabaseLocalTime(dataBase).toEpochMilli());
    }
}
package org.asosat.ddd.storage.mongo;

import static org.corant.shared.util.Assertions.shouldBeTrue;
import static org.corant.shared.util.Strings.contains;
import static org.corant.shared.util.Strings.isNotBlank;
import static org.corant.shared.util.Strings.left;
import static org.corant.shared.util.Strings.right;
import static org.corant.shared.util.Strings.trim;

import com.mongodb.client.MongoDatabase;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.literal.NamedLiteral;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import org.corant.config.declarative.DeclarativeConfigResolver;
import org.corant.context.Instances;
import org.corant.shared.exception.CorantRuntimeException;
import org.corant.shared.normal.Names;
import org.corant.shared.util.Strings;

/**
 * 注册GridFSProvider
 * @author don
 * @date 2020/12/14
 */
public class GridFSExtension implements Extension {

  protected final Logger logger = Logger.getLogger(getClass().getName());

  protected GridFSConfig config;

  void onBeforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
    this.config = DeclarativeConfigResolver.resolveSingle(GridFSConfig.class);
  }

  void onAfterBeanDiscovery(@Observes AfterBeanDiscovery event) {
    if (config.isEnable() && isNotBlank(config.getQualifier())) {
      String fullQualifier = config.getQualifier();
      shouldBeTrue(contains(fullQualifier, Names.NAME_SPACE_SEPARATORS),
                   "GridFSProvider initialize error, please check the value of configuration item['storage.gridfs.database-bucket'], "
                       + "the correct value must contain the database name and bucket name and be connected by '.'");
      for (String qualifier : fullQualifier.split(",")) {
        int lastDot = qualifier.lastIndexOf(Names.NAME_SPACE_SEPARATOR);
        String dataBaseName = trim(left(qualifier, lastDot));
        String bucketName = trim(right(qualifier, qualifier.length() - lastDot - 1));
        event.addBean()
            .addQualifiers(Default.Literal.INSTANCE, Any.Literal.INSTANCE, NamedLiteral.of(bucketName))
            .scope(ApplicationScoped.class)
            .addTransitiveTypeClosure(GridFSProvider.class).beanClass(GridFSProvider.class)
            .produceWith(beans -> {
              MongoDatabase dataBase = Instances.findNamed(MongoDatabase.class, dataBaseName)
                  .orElseThrow(() -> new CorantRuntimeException("Can not find any mongo database by name %s.", bucketName));
              return new GridFSProvider(dataBase, bucketName);
            });
      }
    }
  }
}
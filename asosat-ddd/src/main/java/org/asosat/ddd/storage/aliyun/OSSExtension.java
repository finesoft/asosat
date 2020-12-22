package org.asosat.ddd.storage.aliyun;

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

/**
 * 注册OSSProvider
 * @author don
 * @date 2020/12/16
 */
public class OSSExtension implements Extension {

  protected final Logger logger = Logger.getLogger(getClass().getName());

  protected OSSConfig config;

  void onBeforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
    this.config = DeclarativeConfigResolver.resolveSingle(OSSConfig.class);
  }

  void onAfterBeanDiscovery(@Observes AfterBeanDiscovery event) {
    if (config.isEnable()) {
      String fullBucket = config.getBucket();
      for (String bucket : fullBucket.split(",")) {
        event.addBean()
            .addQualifiers(Default.Literal.INSTANCE, Any.Literal.INSTANCE, NamedLiteral.of(bucket))
            .scope(ApplicationScoped.class)
            .addTransitiveTypeClosure(OSSProvider.class).beanClass(OSSProvider.class)
            .produceWith(beans -> new OSSProvider(config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeySecret(), bucket))
            .destroyWith((b, ctx) -> b.shutdown());
      }
    }
  }
}

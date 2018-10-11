package org.asosat.migrate;

import static org.asosat.kernel.util.MyBagUtils.asSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.configuration.FluentConfiguration;

@ApplicationScoped
public abstract class FlywayMigrator {

  @Inject
  Logger logger;

  @Inject
  @Any
  protected Instance<FlywayConfigProvider> configProviders;

  @Inject
  @Any
  protected Instance<FlywayCallback> callbacks;

  public void migrate() {
    if (this.configProviders.isResolvable()) {
      this.configProviders.stream().map(this::build).forEach(this::doMigrate);
    }
  }

  protected Flyway build(FlywayConfigProvider provider) {
    DataSource ds = provider.getDataSource();
    Collection<String> locations = provider.getLocation();
    Set<String> locationsToUse =
        locations == null ? asSet(this.defaultLocation(ds)) : new HashSet<>(locations);
    FluentConfiguration fc =
        Flyway.configure().dataSource(ds).locations(locationsToUse.toArray(new String[0]));
    if (this.callbacks.isResolvable()) {
      fc.callbacks(this.callbacks.stream().toArray(Callback[]::new));
    }
    this.config(provider, fc);
    return fc.load();
  }

  protected void config(FlywayConfigProvider provider, FluentConfiguration fc) {}

  protected String defaultLocation(DataSource ds) {
    return "classpath:db/migration";
  }

  protected void doMigrate(Flyway flyway) {
    flyway.migrate();
  }

}

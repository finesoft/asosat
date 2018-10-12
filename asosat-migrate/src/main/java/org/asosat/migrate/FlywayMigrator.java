package org.asosat.migrate;

import static org.asosat.kernel.util.MyBagUtils.asSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;
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
  protected Instance<FlywayCallback> callbacks;

  public void migrate() {
    this.logger.fine(() -> "Start migrate process");
    this.getConfigProviders().map(this::build).forEach(this::doMigrate);
  }

  protected Flyway build(FlywayConfigProvider provider) {
    DataSource ds = provider.getDataSource();
    Collection<String> locations = provider.getLocations();
    Set<String> locationsToUse =
        locations == null ? asSet(this.defaultLocation(ds)) : new HashSet<>(locations);
    this.logger.fine(
        () -> String.format("Build flyway instance that data source is %s and location is [%s]",
            ds.toString(), String.join(";", locationsToUse.toArray(new String[0]))));
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
    return "META-INF/dbmigration";
  }

  protected void doMigrate(Flyway flyway) {
    flyway.migrate();
  }

  protected Stream<FlywayConfigProvider> getConfigProviders() {
    return Stream.empty();
  }

}

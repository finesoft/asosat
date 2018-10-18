/*
 * Copyright (c) 2013-2018. BIN.CHEN
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
package org.asosat.migrate;

import static org.asosat.kernel.util.MyBagUtils.asSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.sql.DataSource;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.configuration.FluentConfiguration;

/**
 * asosat-migrate <br/>
 * To disable migrate set "asosat.migrate.enable" true.
 *
 * @author bingo 下午10:19:23
 *
 */
@ApplicationScoped
public abstract class FlywayMigrator {

  @Inject
  @ConfigProperty(name = "asosat.migrate.enable", defaultValue = "false")
  Boolean enable;

  @Inject
  Logger logger;

  @Inject
  @Any
  protected Instance<FlywayCallback> callbacks;

  public void migrate() {
    if (this.enable != null && this.enable.booleanValue()) {
      this.logger.info(() -> "Start migrate process");
      this.getConfigProviders().map(this::build).forEach(this::doMigrate);
    } else {
      this.logger.info(() -> String.format(
          "Disable migrate process, If you want to migrate, set %s in the configuration file!",
          "asosat.migrate.enable=true"));
    }
  }

  protected Flyway build(FlywayConfigProvider provider) {
    DataSource ds = provider.getDataSource();
    Collection<String> locations = provider.getLocations();
    Set<String> locationsToUse =
        locations == null ? asSet(this.defaultLocation(ds)) : new HashSet<>(locations);
    this.logger.info(
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

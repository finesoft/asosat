package org.asosat.migrate;

import java.util.Collection;
import java.util.Set;
import javax.sql.DataSource;

public interface FlywayConfigProvider {

  DataSource getDataSource();

  Collection<String> getLocations();

  public static class DefaultFlywayConfigProvider implements FlywayConfigProvider {

    final DataSource dataSource;
    final Set<String> locations;

    public DefaultFlywayConfigProvider(DataSource dataSource, Set<String> locations) {
      super();
      this.dataSource = dataSource;
      this.locations = locations;
    }

    @Override
    public DataSource getDataSource() {
      return dataSource;
    }

    @Override
    public Set<String> getLocations() {
      return locations;
    }

  }
}

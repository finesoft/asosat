package org.asosat.migrate;

import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;

@ApplicationScoped
public interface FlywayConfigProvider {

  DataSource getDataSource();

  Collection<String> getLocation();

}

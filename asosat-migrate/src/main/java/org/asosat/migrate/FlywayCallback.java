package org.asosat.migrate;

import javax.enterprise.context.ApplicationScoped;
import org.flywaydb.core.api.callback.Callback;

@ApplicationScoped
public interface FlywayCallback extends Callback {

}

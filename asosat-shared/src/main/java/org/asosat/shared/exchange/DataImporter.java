package org.asosat.shared.exchange;

import org.corant.shared.util.Resources.Resource;

/**
 * @author don
 * @date 2020-01-03
 */
public interface DataImporter {

  Resource importData(Resource resource, DataImportReporter reporter);

  interface DataImportReporter {

    void report(ImportContext ctx);
  }

  interface ImportContext {

    Long getReferenceId();

    String getReferenceNumber();

    Integer getRight();

    Integer getError();

    Integer getTotal();
  }
}

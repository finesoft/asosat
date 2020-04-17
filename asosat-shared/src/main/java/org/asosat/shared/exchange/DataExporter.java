package org.asosat.shared.exchange;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author don
 * @date 2020-01-03
 */
public interface DataExporter<C> {

  void exportData(C criteria, OutputStream output) throws IOException;
}

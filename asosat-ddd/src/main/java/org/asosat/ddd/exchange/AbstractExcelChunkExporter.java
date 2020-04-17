package org.asosat.ddd.exchange;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;
import org.asosat.ddd.application.query.GenericQueryParam;
import org.asosat.shared.exchange.DataExporter;
import org.corant.suites.query.shared.QueryService.Forwarding;

/**
 * @author don
 * @date 2020-01-03
 */

public abstract class AbstractExcelChunkExporter<V, C> extends AbstractExcelExchanger<V> implements DataExporter<C> {

  private static final int CHUNK_LIMIT = 1024;

  protected abstract Forwarding<V> chunkForwardQuery(GenericQueryParam<C> param);

  @Override
  public void exportData(C criteria, OutputStream output) throws IOException {
    XSSFWorkbook wb = XSSFWorkbookFactory.createWorkbook();
    int rowIdx = ExcelHelper.initColumn(wb.createSheet(), determineVOClass());

    SXSSFWorkbook workbook = new SXSSFWorkbook(wb, -1);
    try {
      SXSSFSheet sheet = workbook.getSheetAt(0);
      GenericQueryParam<C> param = new GenericQueryParam<>();
      param.setCriteria(criteria).setLimit(CHUNK_LIMIT);
      Forwarding<V> forwarding;
      MutableInt offset = new MutableInt(0);
      do {
        param.setOffset(offset.getAndAdd(param.getLimit()));
        forwarding = chunkForwardQuery(param);
        for (V d : forwarding.getResults()) {
          Row row = sheet.createRow(rowIdx++);
          ExcelHelper.writeVoToRow(d, row);
        }
        sheet.flushRows();
      } while (forwarding.hasNext());
      workbook.write(output);
    } finally {
      workbook.dispose();
    }
  }
}

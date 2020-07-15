package org.asosat.ddd.exchange;

import static org.corant.shared.util.Assertions.shouldBeTrue;
import static org.corant.shared.util.Empties.isNotEmpty;
import static org.corant.shared.util.Lists.listOf;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.asosat.shared.exchange.DataImporter;
import org.corant.shared.exception.CorantRuntimeException;
import org.corant.shared.util.Resources.InputStreamResource;
import org.corant.shared.util.Resources.Resource;
import org.corant.suites.bundle.exception.GeneralRuntimeException;

/**
 * @author don
 * @date 2020-01-04
 */
public abstract class AbstractExcelChunkImporter<V> extends AbstractExcelExchanger<V>
    implements DataImporter {

  private static final int CHUNK_LIMIT = 256;

  private final int numOfSkipHeadRow;

  protected AbstractExcelChunkImporter() {
    this(1);
  }

  protected AbstractExcelChunkImporter(int numOfSkipHeadRow) {
    shouldBeTrue(numOfSkipHeadRow >= 0);
    this.numOfSkipHeadRow = numOfSkipHeadRow;
  }

  /**
   * 导入数据, 捕获GeneralRuntimeException异常,并输出excel,其它异常无法继续执行
   * 
   * @param resource
   * @param reporter
   */
  @Override
  public Resource importData(Resource resource, DataImportReporter reporter) {
    try {
      Workbook workbook;
      try (InputStream input = new BufferedInputStream(resource.openStream())) {
        workbook = WorkbookFactory.create(input);
      }
      Sheet sheet = workbook.getSheetAt(0);
      int numberOfRows = sheet.getPhysicalNumberOfRows();
      HoldingImportContext ctx = new HoldingImportContext(numberOfRows - numOfSkipHeadRow);
      IntegrateReporter integrateReporter = new IntegrateReporter(reporter, ctx);

      importPrepare(ctx);
      Map<Row, V> collected = new LinkedHashMap<>();
      for (int rowIdx = numOfSkipHeadRow; rowIdx < numberOfRows; rowIdx++) {
        Row row = sheet.getRow(rowIdx);
        try {
          V vo = ExcelHelper.readRowToVo(row, determineVOClass());
          collected.put(row, vo);
        } catch (GeneralRuntimeException e) {
          integrateReporter.rowReport(row, e);
        }
        if ((rowIdx % CHUNK_LIMIT == 0 || rowIdx == numberOfRows - 1) && isNotEmpty(collected)) {
          BiConsumer<HoldingImportContext, V> eachRowConsumer =
              peekChunkData(listOf(collected.values()));
          for (Entry<Row, V> ent : collected.entrySet()) {
            GeneralRuntimeException gex = null;
            try {
              eachRowConsumer.accept(ctx, ent.getValue());
            } catch (GeneralRuntimeException e) {
              gex = e;
            } finally {
              integrateReporter.rowReport(ent.getKey(), gex);
            }
          }
          collected.clear();
        }
      }
      // FIXME DON 先临时在内存中 http://poi.apache.org/components/spreadsheet/how-to.html#sxssf
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      workbook.write(output);
      return new InputStreamResource(new ByteArrayInputStream(output.toByteArray()), "result.xlsx");
    } catch (IOException e) {
      throw new CorantRuntimeException(e, "import data io error");
    }
  }

  @SuppressWarnings("unused")
  protected void importPrepare(HoldingImportContext ctx) {}

  protected abstract BiConsumer<HoldingImportContext, V> peekChunkData(List<V> data);

  class IntegrateReporter {

    final HoldingImportContext ctx;
    final DataImportReporter dataImportReporter;

    IntegrateReporter(DataImportReporter dataImportReporter, HoldingImportContext ctx) {
      this.dataImportReporter = dataImportReporter;
      this.ctx = ctx;
    }

    void rowReport(Row row, GeneralRuntimeException gex) {
      if (gex == null) {
        ctx.right++;
      } else {
        ctx.error++;
        logger.log(Level.SEVERE, gex, () -> "excel row error");
      }
      dataImportReporter.report(ctx);
      ExcelHelper.silenceFillResult(row, gex);
    }
  }
}

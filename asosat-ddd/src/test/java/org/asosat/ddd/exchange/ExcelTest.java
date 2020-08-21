package org.asosat.ddd.exchange;

import static org.asosat.ddd.exchange.ExcelColumn.PERCENT_FORMAT;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Data;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;
import org.junit.Test;

/**
 * @author don
 * @date 2020-01-08
 */
public class ExcelTest {

  private static final String path = "C:\\Users\\rnd\\Desktop\\";

  public static class ExcelExchanger extends AbstractExcelExchanger<ExportVO> {

  }

  @Data
  public static class ExportVO {

    @ExcelColumn(alphabet = "a", title = "测试A", extract = true)
    String a;

    @ExcelColumn(alphabet = "B", title = "测试B", dataFormat = PERCENT_FORMAT)
    BigDecimal b;

    //   @ExcelMerge(value = MergeIndicate.START, title = "合并标题")
    @ExcelColumn(alphabet = "C", title = "测试C", extract = true)
    Instant c;

    @ExcelColumn(alphabet = "D", title = "测试D", extract = true)
    LocalDate d;

    //   @ExcelMerge(value = MergeIndicate.END)
    @ExcelColumn(alphabet = "e", title = "测试e", extract = true)
    BigDecimal e;

    @ExcelColumn(alphabet = "f", title = "测试f", extract = true)
    Boolean f;
  }

  @Test
  public void testTmpl() throws IOException {
    OutputStream output = new FileOutputStream("D:/x.xlsx");
    ExcelHelper.generateTemplate(ExportVO.class, output);
  }

  @Test
  public void testResultColumn() throws IOException {
    Workbook workbook;
    try (InputStream input = new FileInputStream(new File(path + "1.xlsx"))) {
      workbook = WorkbookFactory.create(input);
    }
    Sheet sheet = workbook.getSheetAt(0);
    ExcelHelper.silenceFillResult(sheet.getRow(1), null);
    try (OutputStream out = new FileOutputStream(new File(path + Instant.now().getEpochSecond() + ".xlsx"))) {
      workbook.write(out);
      workbook.close();
    }
  }

  @Test
  public void testDetermineVOClass() {
    ExcelExchanger x = new ExcelExchanger();
    System.out.println(x.determineVOClass());
  }

  @Test
  public void testColumn() throws IOException {
    XSSFWorkbook workbook = XSSFWorkbookFactory.createWorkbook();
    Sheet sheet = workbook.createSheet();
    ExcelHelper.initColumn(sheet, ExportVO.class);
    System.out.println(sheet.getPhysicalNumberOfRows());
   /* ExportVO exportVO = new ExportVO();
    exportVO.setC(Instant.now());
    exportVO.setD(LocalDate.now());
    ExcelHelper.writeToRow(exportVO, sheet.createRow(1));*/
    try (OutputStream out = new FileOutputStream(new File(path + Instant.now().getEpochSecond() + ".xlsx"))) {
      workbook.write(out);
      workbook.close();
    }
  }

  @Test
  public void testReadRow() throws IOException, IntrospectionException, InvocationTargetException, IllegalAccessException {
    Workbook workbook;
    try (InputStream input = new FileInputStream(new File(path + "1.xlsx"))) {
      workbook = WorkbookFactory.create(input);
    }
    Sheet sheet = workbook.getSheetAt(0);
    ExportVO vo = ExcelHelper.readRowToVo(sheet.getRow(2), ExportVO.class);
    BeanInfo beanInfo = Introspector.getBeanInfo(ExportVO.class);
    PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
    for (PropertyDescriptor pd : propertyDescriptors) {
      Method getter = pd.getReadMethod();
      if (!pd.getName().equals("class") && getter != null) {
        Object cv = getter.invoke(vo);
        System.out.println(pd.getName() + " " + (cv == null ? "null" : cv.getClass().getName()) + " " + (cv == null ? "null" : cv.toString()));
      }
    }
  }

  @Test
  public void testGetter() throws IntrospectionException, InvocationTargetException, IllegalAccessException {
    ExportVO vo = new ExportVO();
    for (Field field : ExportVO.class.getDeclaredFields()) {
      System.out.println(field.getType());
    }
    BeanInfo beanInfo = Introspector.getBeanInfo(ExportVO.class);
    PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
    for (PropertyDescriptor pd : propertyDescriptors) {
      Method getter = pd.getReadMethod();
      if (getter != null) {
        System.out.println(pd.getName() + " " + getter.invoke(vo));
      }
    }
  }
}

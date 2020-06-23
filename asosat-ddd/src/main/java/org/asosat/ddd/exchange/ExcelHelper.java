package org.asosat.ddd.exchange;

import static java.time.ZoneId.systemDefault;
import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;
import static org.asosat.ddd.exchange.ExcelColumn.DEFAULT_COLUMN_WIDTH;
import static org.asosat.ddd.exchange.ExcelMerge.MergeIndicate.END;
import static org.asosat.ddd.exchange.ExcelMerge.MergeIndicate.START;
import static org.corant.shared.util.Assertions.shouldNotBlank;
import static org.corant.shared.util.Assertions.shouldNotEmpty;
import static org.corant.shared.util.Assertions.shouldNotNull;
import static org.corant.shared.util.Strings.defaultBlank;
import static org.corant.shared.util.Strings.isNotBlank;
import static org.corant.suites.bundle.Preconditions.requireTrue;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.asosat.ddd.MK;
import org.corant.shared.conversion.Conversion;
import org.corant.shared.exception.CorantRuntimeException;
import org.corant.shared.util.Encrypts;
import org.corant.suites.bundle.PropertyEnumerationBundle;
import org.corant.suites.bundle.exception.GeneralRuntimeException;
import org.corant.suites.cdi.Instances;

/**
 * @author don
 * @date 2020-01-03
 */
public class ExcelHelper {

  private static final Map<Class, List<ColumnDesc>> CACHED_COLUMN_DESC = new HashMap<>();

  private static final String RESULT_COLUMN_TITLE = "执行结果";
  private static final String RESULT_SUCCESS_MESSAGE = "执行成功";

  private static final Long EXCEL_MAX_NUMBER = 999999999999999L;// excel只支持15位数字

  /**
   * 初始化Excel列
   * 
   * @param sheet
   * @param voCls
   * @return number of head rows
   */
  public static int initColumn(Sheet sheet, Class<?> voCls) {
    Workbook workbook = sheet.getWorkbook();
    Drawing drawing = sheet.createDrawingPatriarch();
    Row head = sheet.createRow(0), mergeHead = null;
    CellRangeAddress cellRange = null;
    List<ColumnDesc> allColumnDesc = getColumnDesc(voCls);
    for (ColumnDesc colDesc : allColumnDesc) {
      ExcelColumn annotation = colDesc.annotation;
      int width = annotation.width();
      String format = colDesc.annotation.dataFormat();
      Class<?> type = colDesc.field.getType();
      if (type.equals(Instant.class)) {
        format = defaultBlank(format, ExcelColumn.INSTANT_FORMAT);
        width = width == DEFAULT_COLUMN_WIDTH ? 4500 : width;
      } else if (type.equals(LocalDate.class)) {
        format = defaultBlank(format, ExcelColumn.DATE_FORMAT);
        width = width == DEFAULT_COLUMN_WIDTH ? 3000 : width;
      }
      sheet.setColumnWidth(colDesc.index, width);
      if (isNotBlank(format)) {
        CellStyle columnStyle = workbook.createCellStyle();
        columnStyle.setDataFormat(workbook.createDataFormat().getFormat(format));
        sheet.setDefaultColumnStyle(colDesc.index, columnStyle);
      }
      CellStyle cellStyle = workbook.createCellStyle();
      cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      cellStyle.setAlignment(HorizontalAlignment.CENTER);
      cellStyle.setFillForegroundColor(annotation.color().getIndex());
      cellStyle.setBorderTop(BorderStyle.THIN);
      cellStyle.setBorderRight(BorderStyle.THIN);
      cellStyle.setBorderBottom(BorderStyle.THIN);
      cellStyle.setBorderLeft(BorderStyle.THIN);
      if (colDesc.mergeAnnotation != null) {
        if (mergeHead == null) {
          sheet.shiftRows(0, sheet.getLastRowNum(), 1);
          mergeHead = sheet.createRow(0);
          for (int j = 0; j < allColumnDesc.size(); j++) {
            mergeHead.createCell(j, CellType.STRING).setCellStyle(cellStyle);
          }
        }
        Cell mergeHeadCell = mergeHead.getCell(colDesc.index, CREATE_NULL_AS_BLANK);
        mergeHeadCell.setCellStyle(cellStyle);
        if (colDesc.mergeAnnotation.value() == START) {
          mergeHeadCell.setCellValue(colDesc.mergeAnnotation.title());
          cellRange = new CellRangeAddress(0, 0, colDesc.index, colDesc.index);
        } else if (colDesc.mergeAnnotation.value() == END) {
          cellRange.setLastColumn(colDesc.index);
          sheet.addMergedRegion(cellRange);
        }
      }
      Cell cell = head.createCell(colDesc.index, CellType.STRING);
      cell.setCellValue(annotation.title());
      cell.setCellStyle(cellStyle);
      if (annotation.extract()) {
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 4, 2, 6, 4);
        Comment comment = drawing.createCellComment(anchor);
        comment.setAddress(head.getRowNum(), colDesc.index);
        comment.setString(workbook.getCreationHelper().createRichTextString("importable"));
        cell.setCellComment(comment);
      }
    }
    return sheet.getPhysicalNumberOfRows();
  }

  /**
   * 读取excel行转成vo数据
   * 
   * @param row
   * @param voCls
   * @param <V>
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <V> V readRowToVo(Row row, Class<V> voCls) {
    try {
      V vo = voCls.getConstructor().newInstance();
      for (ColumnDesc colDesc : getColumnDesc(voCls)) {
        ExcelColumn annotation = colDesc.annotation;
        if (annotation.extract()) {
          Class<?> type = colDesc.field.getType();
          Cell cell = row.getCell(colDesc.index, MissingCellPolicy.RETURN_NULL_AND_BLANK);
          if (cell != null) {
            try {
              Object v;
              switch (cell.getCellType()) {
                case STRING:
                  v = cell.getStringCellValue();
                  if (Enum.class.isAssignableFrom(type) && annotation.enumLiteral()) {// 字符转换枚举
                    Optional<PropertyEnumerationBundle> opt =
                        Instances.find(PropertyEnumerationBundle.class);// FIXME DON
                    if (opt.isPresent()) {
                      Map<Enum, String> literals =
                          opt.get().getEnumItemLiterals((Class<Enum>) type, Locale.getDefault());
                      shouldNotEmpty(literals,
                          "enum:" + type + " not configured literal properties");
                      for (Entry<Enum, String> e : literals.entrySet()) {
                        if (e.getValue().equalsIgnoreCase((String) v)) {
                          v = e.getKey();
                          break;
                        }
                      }
                      requireTrue(v instanceof Enum, MK.EXCEL_CELL_ENUM_NOT_MATCH);
                    }
                  }
                  break;
                case NUMERIC:
                  if (Temporal.class.isAssignableFrom(type)) {
                    v = cell.getDateCellValue();
                  } else {
                    v = cell.getNumericCellValue();
                  }
                  break;
                case BOOLEAN:
                  v = cell.getBooleanCellValue();
                  break;
                case BLANK:
                  v = null;
                  break;
                default:
                  throw new CorantRuntimeException(
                      "excel get cell not support " + cell.getCellType());
              }
              colDesc.setter.invoke(vo, Conversion.convert(v, type));
            } catch (RuntimeException e) {
              throw new GeneralRuntimeException(e, MK.EXCEL_CELL_ERROR, annotation.alphabet(),
                  annotation.title());
            }
          }
        }
      }
      return vo;
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException
        | NoSuchMethodException e) {
      throw new CorantRuntimeException(e);
    }
  }

  /**
   * 将处理结果写入excel最后一列
   * 
   * @param row
   * @param gex
   */
  public static void silenceFillResult(Row row, GeneralRuntimeException gex) {
    Workbook workbook = row.getSheet().getWorkbook();
    Sheet sheet = row.getSheet();
    Row headRow = sheet.getRow(0);

    int lastIdx = headRow.getLastCellNum() - 1;
    Cell lastHeadColumn = headRow.getCell(lastIdx, CREATE_NULL_AS_BLANK);
    if (!lastHeadColumn.toString().startsWith(RESULT_COLUMN_TITLE)) {
      lastHeadColumn = headRow.getCell(++lastIdx, CREATE_NULL_AS_BLANK);
      lastHeadColumn.setCellValue(RESULT_COLUMN_TITLE);
      sheet.setColumnWidth(lastIdx, 8192);

      CellStyle cellStyle = workbook.createCellStyle();
      cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      cellStyle.setFillForegroundColor(IndexedColors.RED1.getIndex());
      lastHeadColumn.setCellStyle(cellStyle);
    }
    Cell lastCell = row.getCell(lastIdx, CREATE_NULL_AS_BLANK);
    if (gex == null) {
      lastCell.setCellValue(RESULT_SUCCESS_MESSAGE);
    } else {
      lastCell.setCellStyle(lastHeadColumn.getCellStyle());
      lastCell.setCellValue(gex.getLocalizedMessage());
    }
  }

  /**
   * 设置Excel每一数据行的值
   * 
   * @param row
   * @param vo
   */
  public static void writeVoToRow(Object vo, Row row) {
    for (ColumnDesc colDesc : getColumnDesc(vo.getClass())) {
      Cell cell = row.createCell(colDesc.index);
      cell.setCellStyle(row.getSheet().getColumnStyle(colDesc.index));

      Object v;
      try {
        v = colDesc.getter.invoke(vo);
        if (v == null) {
          continue;
        }
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new CorantRuntimeException(e);
      }
      if (v instanceof String) {
        cell.setCellValue(v.toString());
      } else if (v instanceof Number) {
        if (v instanceof Long && ((Long) v).compareTo(EXCEL_MAX_NUMBER) > 0) {
          cell.setCellValue(v.toString());
        } else {
          cell.setCellValue(((Number) v).doubleValue());
        }
      } else if (v instanceof Instant) {
        cell.setCellValue(LocalDateTime.ofInstant((Instant) v, systemDefault()));
      } else if (v instanceof LocalDate) {
        cell.setCellValue((LocalDate) v);
      } else if (v instanceof Enum) {
        Optional<PropertyEnumerationBundle> opt; // FIXME DON 待提供静态变量后再改进 Instances.resolve
        if (colDesc.annotation.enumLiteral()
            && (opt = Instances.find(PropertyEnumerationBundle.class)).isPresent()) {
          String literal = opt.get().getEnumItemLiteral((Enum) v, Locale.getDefault());
          shouldNotBlank(literal, "enum:" + v + " not configured literal properties");
          cell.setCellValue(literal);
        } else {
          cell.setCellValue(v.toString());
        }
      } else if (v instanceof Boolean) {
        cell.setCellValue((Boolean) v);
      } else {
        throw new CorantRuntimeException("excel set cell not support " + v.getClass());
      }
    }
  }

  private static List<ColumnDesc> getColumnDesc(Class<?> voCls) {
    List<ColumnDesc> allColumnDesc;
    if ((allColumnDesc = CACHED_COLUMN_DESC.get(voCls)) == null) {
      synchronized (ExcelHelper.class) {
        if ((allColumnDesc = CACHED_COLUMN_DESC.get(voCls)) == null) {
          CACHED_COLUMN_DESC.put(voCls, allColumnDesc = new ArrayList<>());
          BeanInfo beanInfo;
          try {
            beanInfo = Introspector.getBeanInfo(voCls);
          } catch (IntrospectionException e) {
            throw new CorantRuntimeException(e);
          }
          for (Field field : voCls.getDeclaredFields()) {
            ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
            if (annotation != null) {
              for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                if (field.getName().equals(pd.getName())) {
                  Method getter = pd.getReadMethod(), setter = pd.getWriteMethod();
                  if (getter != null) {
                    allColumnDesc.add(new ColumnDesc(annotation, field, getter, setter));
                  }
                  break;
                }
              }
            }
          }
        }
      }
    }
    return allColumnDesc;
  }

  private static class ColumnDesc {

    private final int index;
    private final ExcelColumn annotation;
    private final ExcelMerge mergeAnnotation;
    private final Field field;
    private final Method getter;
    private final Method setter;

    public ColumnDesc(ExcelColumn annotation, Field field, Method getter, Method setter) {
      index = Encrypts.alphabetToIntScale(annotation.alphabet()) - 1;
      this.annotation = annotation;
      mergeAnnotation = field.getAnnotation(ExcelMerge.class);
      this.field = field;
      this.getter = getter;
      this.setter = setter;
      if (annotation.extract()) {
        shouldNotNull(setter, field + "没有setter方法");
      }
    }
  }
}

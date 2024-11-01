package org.example.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.utils.annotation.ExcelColumn;
import org.example.utils.annotation.ReferenceColumn;
import org.example.utils.annotation.SheetData;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@Component
@Slf4j
public class ExcelUtils {

    private static final String ERROR_REPORT_SYSTEM_EXPORT_DSAI_FILE_NOT_FOUND = "error.report_system.export.dsai.file_not_found";
    private static final String ERROR_REPORT_SYSTEM_CHECK_TEMPLATE = "error.report_system.check_template";
    private static final String ERROR_WHEN_EXPORT_EXCEL = "Error when export excel";

    public static final String LEFT = "LEFT";
    public static final String RIGHT = "RIGHT";
    public static final String CENTER = "CENTER";
    public static final String MONEY_XXX = "MONEY_XXX";
    private static final String FONT_TIMES_NEW_ROMAN = "Times new roman";

    public static Workbook getWorkbook(String path, boolean isFromResources) {
        try (InputStream inputStream = (!isFromResources) ? new FileInputStream(path) : ExcelUtils.class.getClassLoader().getResourceAsStream(path);) {
            Workbook workbook = getWorkbookByType(path, inputStream);
            return workbook;
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return null;
    }

    public static Workbook getWorkbookByType(String fileName, InputStream is) {
        if (is == null) return null;
        Workbook wb = null;
        try {
            if (fileName.endsWith("xls")) {
                //97 (-2007)
                wb = new HSSFWorkbook(is);
            } else if (fileName.endsWith("xlsx")) {
                //2007
                wb = new XSSFWorkbook(is);
            }
            is.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return wb;
    }

    public static ByteArrayResource toByteArrayResource(Workbook workbook) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            workbook.write(stream);
            return new ByteArrayResource(stream.toByteArray());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static Reference reference(Sheet sheet) {
        return new Reference(sheet);
    }

    @Data
    public static class Reference {
        private Sheet sheet;
        private CellStyle style;

        public Reference(Sheet sheet) {
            this.sheet = sheet;
        }

        public Reference cache() {
            Font font = sheet.getWorkbook().createFont();
            font.setFontName(FONT_TIMES_NEW_ROMAN);
            font.setFontHeightInPoints((short) 12);
            CellStyle cellStyleCommon = sheet.getWorkbook().createCellStyle();
            createCellDefault(font, cellStyleCommon);
            style = cellStyleCommon;
            return this;
        }

        public <T> Reference data(List<T> dataReferences) {
            Iterator<Row> rows = this.sheet.rowIterator();
            ExcelUtils.ignoreRow(2, rows);
            int count = 0;
            List<ReferenceColumn> referenceColumns = new ArrayList<>();
            if (!dataReferences.isEmpty()) {
                T first = dataReferences.get(0);
                referenceColumns = ReflectionUtil.getAll(first.getClass());
            }
            for (T dataReference : dataReferences) {
                Row row;
                if (rows.hasNext()) {
                    row = rows.next();
                } else {
                    row = this.sheet.createRow(count + 2);
                }
                ExcelUtils.createCellColor(String.valueOf(count + 1), row, 0, this.style);
                for (ReferenceColumn referenceColumn : referenceColumns) {
                    ExcelUtils.createCellColor(ReflectionUtil.execGetMethod(dataReference, referenceColumn.value(), false), row, referenceColumn.col(), this.style);
                }
                count++;
            }
            return this;
        }
    }

    private static void createCellDefault(Font font, CellStyle cellStyleCommon) {
        createDefaultCell(font, cellStyleCommon);
    }

    private static void createDefaultCell(Font fNotBold, CellStyle cellStyleCommon) {
        cellStyleCommon.setFont(fNotBold);
        cellStyleCommon.setWrapText(true);
        cellStyleCommon.setBorderTop(CellStyle.BORDER_THIN);
        cellStyleCommon.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyleCommon.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyleCommon.setBorderRight(CellStyle.BORDER_THIN);
        cellStyleCommon.setAlignment(CellStyle.BORDER_THIN);
        cellStyleCommon.setVerticalAlignment(CellStyle.BORDER_THIN);
    }

    public static void dataValidationExport(Workbook workbook, String dataValidationConstraint, int columnValidation) { // Tạo kiểm tra dữ liệu tùy chỉnh trỏ đến sheet tham chiếu
        DataValidationHelper validationHelper = workbook.getSheetAt(0).getDataValidationHelper();
        DataValidationConstraint validationConstraint = validationHelper.createFormulaListConstraint(dataValidationConstraint); // Thiết lập phạm vi áp dụng kiểm tra dữ liệu
        CellRangeAddressList addressList = new CellRangeAddressList(workbook.getSheetAt(0).getFirstRowNum(), SpreadsheetVersion.EXCEL2007.getLastRowIndex(), columnValidation, columnValidation);
        // Tạo kiểm tra dữ liệu
        DataValidation dataValidation = validationHelper.createValidation(validationConstraint, addressList);
        // Thiết lập thông điệp khi kiểm tra dữ liệu không hợp lệ
        dataValidation.createErrorBox("Invalid Value", "Please select a value from the list.");
        // Thiết lập cách xử lý khi kiểm tra dữ liêu không hợp lệ
        dataValidation.setShowErrorBox(true);
        dataValidation.setShowPromptBox(true);
        // Áp dụng kiểm tra dữ liệu vào ô A1
        workbook.getSheetAt(0).addValidationData(dataValidation);
    }

    public static void ignoreRow(int endRow, Iterator<Row> rows) {
        for (int i = 0; i < endRow; i++) {
            if (rows.hasNext()) {
                rows.next();
            } else {
                return;
            }
        }
    }

    public static void createCellColor(String cellValue, Row row, int index, CellStyle cellStyle) {
        Cell tempCell = row.createCell(index);
        tempCell.setCellValue(cellValue);
        tempCell.setCellStyle(cellStyle);
    }

    public static void getListHeader(Workbook workbook, DataFormatter dataFormatter, List<Integer> listHeadersRequire, List<String> listHeaders, AtomicInteger rowHeader) {
        int countRow = 0;
        for (Row row : workbook.getSheetAt(0)) {
            boolean checkHeader = false;
            int countCell = 0;
            for (Cell cell : row) {
                String data = dataFormatter.formatCellValue(cell).trim();
                if (data.equals("STT")) {
                    checkHeader = true;
                    rowHeader.set(countRow);
                }
                if (checkHeader && StringUtils.isNoneBlank(data)) {
                    int k = 0;
                    k = data.indexOf("*");
                    data = data.replace("?", "");
                    if (k >= 0)
                        listHeadersRequire.add(countCell);
                    else k = data.indexOf("(");
                    String attributeHeaderCustom = null;

                    if (k != -1) {
                        attributeHeaderCustom = data.substring(0, k - 1);
                        listHeaders.add(attributeHeaderCustom.trim());
                    } else {
                        listHeaders.add(data.trim());
                    }
                }
                countCell++;
            }
            countRow++;
            if (checkHeader) break;
        }
    }

    public static final void getListHeaderIndex(Workbook workbook, DataFormatter dataFormatter, List<Integer> listHeadersRequire, List<String> listHeaders, AtomicInteger rowHeader) {
        int countRow = 0;
        for (int i = 0; i <= workbook.getSheetAt(0).getLastRowNum(); i++) {
            boolean checkHeader = false;
            int countCell = 0;
            countRow++;
            if (workbook.getSheetAt(0).getRow(i) == null) continue;
            for (Cell cell : workbook.getSheetAt(0).getRow(i)) {
                String data = dataFormatter.formatCellValue(cell).trim();
                if (data.equals("STT")) {
                    checkHeader = true;
                    rowHeader.set(countRow - 1);
                }
                if (checkHeader && StringUtils.isNoneBlank(data)) {
                    int k = 0;
                    k = data.indexOf("*");
                    data = data.replace("?", "");
                    if (k >= 0)
                        listHeadersRequire.add(countCell);
                    else k = data.indexOf("(");
                    String attributeHeaderCustom = null;
                    if (k != -1) {
                        attributeHeaderCustom = data.substring(0, k - 1);
                        listHeaders.add(attributeHeaderCustom.trim());
                    } else {
                        listHeaders.add(data.trim());
                    }
                }
                countCell++;
            }
            if (checkHeader) break;
        }
    }

    public static void writeCell(XSSFSheet sheet, int rowIndex, int cellIndex, String value, int typeSheet, boolean header, int typeTemplate) {
        XSSFRow newRow = sheet.getRow(rowIndex);
        if (newRow != null) {
            Cell cell = newRow.getCell(cellIndex);
            if (cell == null) {
                cell = newRow.createCell(cellIndex);
            }
            if (header) {
                CellStyle cellStyle = cell.getCellStyle();
                if (cellStyle == null) {
                    cellStyle = cell.getSheet().getWorkbook().createCellStyle();
                }
                cellStyle.setFillBackgroundColor(IndexedColors.YELLOW.index);
                cellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
                cellStyle.setWrapText(true);
                cell.setCellStyle(cellStyle);
            }
            cell.setCellValue(value);
        }
    }

    public static String formatCellValue(Cell cell) {
        String val = "";
        if (cell != null) {
            if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                if (cell.getCachedFormulaResultType() == Cell.CELL_TYPE_STRING || cell.getCachedFormulaResultType() ==
                        Cell.CELL_TYPE_BOOLEAN || cell.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    val = String.valueOf(cell.getRichStringCellValue());
                }
            } else if (cell.getCellType() == Cell.CELL_TYPE_ERROR) {
                log.error(cell.toString());
                val = cell.toString();
            } else {
                DataFormatter dataFormatter = new DataFormatter();
                val = dataFormatter.formatCellValue(cell);
            }
        }
        return val != null ? val.trim() : "";
    }

    public static Date convertToDateCatchException(String date) {
        date = date.replaceAll("/", "-");
        String[] dateFormats = {"dd-MM-yyyy", "d-M-yyyy"};
        // Regular expressions to match the expected date formats
        String[] datePatterns = {"\\d{2}-\\d{2}-\\d{4}", "\\d{1}-\\d{1}-\\d{4}"};
        // Iterate over date formats to parse the date
        for (int i = 0; i < dateFormats.length; i++) {
            String format = dateFormats[i];
            String pattern = datePatterns[i];
            if (Pattern.matches(pattern, date)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                simpleDateFormat.setLenient(false);
                // To ensure strict date parsing
                System.out.println("Trying format: " + format);
                try {
                    Date parsedDate = simpleDateFormat.parse(date);
                    return parsedDate;
                } catch (ParseException ex) {
                    log.error("Date " + date + "does not match pattern" + pattern, ex);
                }
            } else {
                System.err.println("Date " + date + " does not match pattern" + pattern);
            }
        }
        return null;
    }

    public static Date convertToDate(String date) {
        date = date.replaceAll("/", "-");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException ex) {
            try {
                simpleDateFormat = new SimpleDateFormat("d-M-yyyy");
                return simpleDateFormat.parse(date);
            } catch (ParseException e) {
                return null;
            }
        }
    }

    public static final ByteArrayResource downloadTemplateV2(String path) throws IOException {
        // Tạo đối tượng File từ đường dẫn tập tin
        File file = new File(path);
        // Đọc tập tin Excel vào đối tượng Workbook
        FileInputStream inputStream = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(inputStream);
//        Workbook workbook =ExcelUtils.getWorkbook(path, true);
        if (workbook != null) {
            return ExcelUtils.toByteArrayResource(workbook);
        }
        return null;
    }

    public static ByteArrayResource exportLargeDataChecklist(List<SheetData> sheetDatas, String templatePath) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(downloadResource(templatePath))) {
            XSSFWorkbook coreWork = new XSSFWorkbook(bis);
            SXSSFWorkbook workbook = new SXSSFWorkbook(coreWork, SXSSFWorkbook.DEFAULT_WINDOW_SIZE);
            Map<String, CellStyle> mapStyle = mapStyle(workbook);
            for (SheetData sheetData : sheetDatas) {
                Map<String, ExcelColumn> map = getHeaderByClass(sheetData.getClazz());
                SXSSFSheet sheet = (SXSSFSheet) workbook.getSheet(sheetData.getSheetName());
                int ignore = sheetData.getIgnoreRowHeader();
                for (Object t : sheetData.getData()) {
                    SXSSFRow newRow = (SXSSFRow) sheet.createRow(ignore++);
                    createLargeDataCheckList(newRow, map, mapStyle, t);
                }
            }
            return returnToResource(workbook);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException(ERROR_WHEN_EXPORT_EXCEL); //NOSONAR
        }
    }

    public static byte[] downloadResource(String resourcePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        try (InputStream inputStream = resource.getInputStream()) {
            return IOUtils.toByteArray(inputStream);
        }
    }

    static Map<String, CellStyle> mapStyle(Workbook workbook) {
        Map<String, CellStyle> functionStyle = new HashMap<>();
        functionStyle.put(LEFT, ExcelUtils.styleLeft(workbook));
        functionStyle.put(RIGHT, ExcelUtils.styleRight(workbook));
        functionStyle.put(CENTER, ExcelUtils.styleCenter(workbook));
        functionStyle.put(MONEY_XXX, ExcelUtils.styleMoneyXXX(workbook));
        return functionStyle;
    }


    static CellStyle styleLeft(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = getFontDefault(workbook);
        style.setFont(font);
        style.setWrapText(true);
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        fullBorder(style);
        return style;
    }

    static CellStyle styleRight(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = getFontDefault(workbook);
        style.setFont(font);
        style.setWrapText(true);
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        fullBorder(style);
        return style;
    }


    static CellStyle styleMoneyXXX(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = getFontDefault(workbook);
        style.setFont(font);
        style.setWrapText(true);
        style.setAlignment(CellStyle.ALIGN_RIGHT);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("#,##0.00\\ \"\"; \\-#,##0.00\\ \"\""));
        fullBorder(style);
        return style;

    }

    static CellStyle styleCenter(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = getFontDefault(workbook);
        style.setFont(font);
        style.setWrapText(true);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        fullBorder(style);
        return style;
    }

    public static void fullBorder(CellStyle cellstyle) {
        cellstyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellstyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellstyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellstyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    }


    public static Font getFontDefault(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontName(FONT_TIMES_NEW_ROMAN);
        font.setFontHeightInPoints((short) 12);
        return font;
    }


    private static Map<String, ExcelColumn> getHeaderByClass(Class<?> clazz) {
        Map<String, ExcelColumn> result = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof ExcelColumn) {
                    ExcelColumn col = (ExcelColumn) annotation;
                    result.put(field.getName(), col);
                    break;
                }
            }
        }
        return result;
    }

    private static <T> void createLargeDataCheckList(Row row, Map<String, ExcelColumn> map, Map<String, CellStyle> mapStyle, T t) {
        for (Map.Entry<String, ExcelColumn> entry : map.entrySet()) {
            String methodName = "get" + (entry.getKey().charAt(0) + "").toUpperCase() + entry.getKey().substring(1);
            Cell cell = row.createCell(entry.getValue().col());
            ExcelColumn excelColumn = entry.getValue();
            CellStyle cellstyle = mapStyle.get(excelColumn.style());
            try {
                Method method = t.getClass().getMethod(methodName);
                switch (excelColumn.type()) {
                    case _STRING: {
                        String value = (String) method.invoke(t);
                        cell.setCellValue(value);
                        break;
                    }
                    case _DATE: {
                        Date value = (Date) method.invoke(t);
                        cell.setCellValue(value);
                        break;
                    }
                    case _DOLLARS: {
                        BigDecimal value = (BigDecimal) method.invoke(t);
                        cell.setCellValue(value.doubleValue());
                        break;
                    }
                    case _DOUBLE: {
                        Double value = DataUtils.safeToDouble(method.invoke(t));
                        cell.setCellValue(value);
                        break;
                    }
                    case _INTEGER: {
                        Long value = DataUtils.safeToLong(method.invoke(t));
                        cell.setCellValue(value);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            } catch (NullPointerException e) {
                cell.setCellValue("");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                cell.setCellStyle(cellstyle);
            }
        }
    }

    private static ByteArrayResource returnToResource(Workbook workbook) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            workbook.write(bos);
            return new ByteArrayResource(bos.toByteArray());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(ERROR_WHEN_EXPORT_EXCEL); //NOSONAR
        }
    }

}

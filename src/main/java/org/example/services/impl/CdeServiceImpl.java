package org.example.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.dtos.reponse.ActionAuditRequest;
import org.example.dtos.reponse.ActionAuditRequest.ActionCode;
import org.example.dtos.reponse.CdeExportExcel;
import org.example.dtos.reponse.CdeResponse;
import org.example.dtos.reponse.ValidateFileResponse;
import org.example.dtos.request.CdeRequest;
import org.example.entities.*;
import org.example.repositories.*;
import org.example.services.CdeService;
import org.example.utils.CommonUtils;
import org.example.utils.ExcelUtils;
import org.example.utils.annotation.SheetData;
import org.example.utils.common.Constants;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CdeServiceImpl implements CdeService {

    static String TEMPLATE_PATH = "excel_template/cde_template.xlsx";
    static String IMPORT_CDE = "import cde";
    static final String FILE_INVALID = "File invalid";

    private static final String TEMPLATE_PATH_EXPORT = "excel_template/cde_export.xlsx";
    private static final String ENTITY = "Cde";
    private static final String SYSTEM = "vdg";
    private static final String CAS_FULL_NAME = "cas:fullName";
    private static final String FULL_NAME = "fullName";
    private static final String MUST_NUMBER = "phải là số\n";
    private static final String NOT_BELONG_LIST = " không thuộc danh sách\n";

    BusinessProcessRepo businessProcessRepo;
    CdeRepo cdeRepo;
    CdeTypeRepo cdeTypeRepo;
    DatabaseRepo databaseRepo;
    DataGroupRepo dataGroupRepo;
    DepartmentRepo departmentRepo;
    ItSystemRepo itSystemRepo;
    ReportRepo reportRepo;
    StaffRepo staffRepo;
    ConfidentialLevelRepo confidentialLevelRepo;
    DataCategoryRepo dataCategoryRepo;
    ObjectMapper mapper;

    @Override
    public ByteArrayResource downloadTemplate() {
        Workbook workbook = ExcelUtils.getWorkbook(TEMPLATE_PATH, true);
        if (workbook != null) {
            this.generateReference(workbook);
            return ExcelUtils.toByteArrayResource(workbook);
        }
        return null;
    }

    private void generateReference(Workbook workbook) {
        List<ItSystem> systems = itSystemRepo.findAll();
        List<Database> databases = databaseRepo.findAll();
        List<Department> owners = departmentRepo.findAll();
        List<DataGroup> dataGroups = dataGroupRepo.findAll();
        List<BusinessProcess> wikiBusinessProcesses = businessProcessRepo.findAll();
        List<Report> reports = reportRepo.findAll();
        List<Staff> wikiStaffs = staffRepo.findAll();
        List<CdeType> criticalDataElementTypes = cdeTypeRepo.findAll();
        List<ConfidentialLevel> confidentialLevels = confidentialLevelRepo.findAll();
        List<DataCategory> dataCategories = dataCategoryRepo.findAll();

        int referenceSheetIdx = 1;

        ExcelUtils.reference(workbook.getSheetAt(referenceSheetIdx++)).cache().data(systems);
        ExcelUtils.reference(workbook.getSheetAt(referenceSheetIdx++)).cache().data(databases);
        ExcelUtils.reference(workbook.getSheetAt(referenceSheetIdx++)).cache().data(owners);
        ExcelUtils.reference(workbook.getSheetAt(referenceSheetIdx++)).cache().data(dataGroups);
        ExcelUtils.reference(workbook.getSheetAt(referenceSheetIdx++)).cache().data(wikiBusinessProcesses);
        ExcelUtils.reference(workbook.getSheetAt(referenceSheetIdx++)).cache().data(reports);
        ExcelUtils.reference(workbook.getSheetAt(referenceSheetIdx++)).cache().data(wikiStaffs);
        ExcelUtils.reference(workbook.getSheetAt(referenceSheetIdx++)).cache().data(criticalDataElementTypes);
        ExcelUtils.reference(workbook.getSheetAt(referenceSheetIdx++)).cache().data(dataCategories);
        ExcelUtils.reference(workbook.getSheetAt(referenceSheetIdx)).cache().data(confidentialLevels);

        String dataValidationTypeReportSys = "'reporting_system'!$D$" + 3 + ":$D$" + (systems.size() + 2);
        ExcelUtils.dataValidationExport(workbook, dataValidationTypeReportSys, 3);

        String dataValidationTypeDB = "'database'!$C$" + 3 + ":$C$" + (databases.size() + 2);
        ExcelUtils.dataValidationExport(workbook, dataValidationTypeDB, 4);

        String dataValidationTypeDataOwner = "'owner'!$D$" + 3 + ":$D$" + (owners.size() + 2);
        ExcelUtils.dataValidationExport(workbook, dataValidationTypeDataOwner, 7);
        ExcelUtils.dataValidationExport(workbook, dataValidationTypeDataOwner, 8);
        ExcelUtils.dataValidationExport(workbook, dataValidationTypeDataOwner, 9);

        String dataValidationTypeDataDomain = "'data_domain'!$C$" + 3 + ":$c$" + (dataGroups.size() + 2);
        ExcelUtils.dataValidationExport(workbook, dataValidationTypeDataDomain, 10);

        String dataValidationTypeBusinessProcess = "'business_process'!$C$" + 3 + ":$C$" + (dataGroups.size() + 2);
        ExcelUtils.dataValidationExport(workbook, dataValidationTypeBusinessProcess, 11);

        String dataValidationTypeReport = "'report'!$C$" + 3 + ":$c$" + (reports.size() + 2);
        ExcelUtils.dataValidationExport(workbook, dataValidationTypeReport, 12);

        String dataValidationTypeSteward = "'data_steward'!$D$" + 3 + ":$D$" + (wikiStaffs.size() + 2);
        ExcelUtils.dataValidationExport(workbook, dataValidationTypeSteward, 13);
        ExcelUtils.dataValidationExport(workbook, dataValidationTypeSteward, 14);
        ExcelUtils.dataValidationExport(workbook, dataValidationTypeSteward, 15);

        String dataValidationCdeType = "'cde_type'!$C$" + 3 + ":$c$" + (criticalDataElementTypes.size() + 2);
        ExcelUtils.dataValidationExport(workbook, dataValidationCdeType, 17);

        String dataValidationConfidentialLevel = "'confidential_level'!$C$" + 3 + ":$C$" + (confidentialLevels.size() + 2);
        ExcelUtils.dataValidationExport(workbook, dataValidationConfidentialLevel, 19);

        String dataValidationDataCategory = "'data_category'!$C$" + 3 + ":$C$" + (dataCategories.size() + 2);
        ExcelUtils.dataValidationExport(workbook, dataValidationDataCategory, 20);
    }

    @Override
    public void validateFile(MultipartFile file, ValidateFileResponse validateFileResponse, AtomicInteger typeResponse, AtomicBoolean checkData, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        try {
            //set data log
            Timestamp startTime = new Timestamp(System.currentTimeMillis());
            StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
//            Config again
            String userName = Constants.LOGIN_DEFAULT;
            ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
            Workbook workbook = new XSSFWorkbook(bis);
            int sheetSize = workbook.getNumberOfSheets();
            if (sheetSize == 11) {
                ActionAuditRequest actionAuditRequest = new ActionAuditRequest(httpServletRequest, httpServletResponse, null, IMPORT_CDE, "01", ActionCode.READ, this.getClass().getName(), String.valueOf(stackTraceElement.getLineNumber()), stackTraceElement.getMethodName(), 0, "void", file, startTime, new Timestamp(System.currentTimeMillis()), 417L, FILE_INVALID, userName);
                log.info("actionAuditRequest1: {}", actionAuditRequest);
                typeResponse.set(Constants.FILE_IMPORT_INVALID);
                return;
            }
            DataFormatter dataFormatter = new DataFormatter();
            List<Integer> listHeadersRequire = new ArrayList<>();
            List<String> listHeaders = new ArrayList<>();
            Workbook workbookTemplate = ExcelUtils.getWorkbook(TEMPLATE_PATH, true);
            List<String> listHeadersTemplate = new ArrayList<>();
            AtomicInteger rowHeader = new AtomicInteger(0);

            ExcelUtils.getListHeader(workbookTemplate, dataFormatter, new ArrayList<>(), listHeadersTemplate, new AtomicInteger(0));
            ExcelUtils.getListHeaderIndex(workbook, dataFormatter, listHeadersRequire, listHeaders, rowHeader);

            Map<String, Department> owners = departmentRepo.findAll().stream().collect(Collectors.toMap(item -> item.getCode(), o -> o, (existing, replacement) -> existing));
            Map<String, Database> databases = databaseRepo.findAll().stream().collect(Collectors.toMap(Database::getName, Function.identity()));
            Map<String, BusinessProcess> businessProcesses = businessProcessRepo.findAll().stream().collect(Collectors.toMap(BusinessProcess::getName, Function.identity()));
            Map<String, DataGroup> dataGroups = dataGroupRepo.findAll().stream().collect(Collectors.toMap(DataGroup::getName, Function.identity()));
            Map<String, ItSystem> systems = itSystemRepo.findAll().stream().collect(Collectors.toMap(s -> s.getCode(), Function.identity()));
            Map<String, Report> reports = reportRepo.findAll().stream().collect(Collectors.toMap(Report::getName, Function.identity(), (existing, replacement) -> existing));
            Map<String, CdeType> cdeTypes = cdeTypeRepo.findAll().stream().collect(Collectors.toMap(CdeType::getName, Function.identity()));
            Map<String, ConfidentialLevel> confidentialLevels = confidentialLevelRepo.findAll().stream().collect(Collectors.toMap(ConfidentialLevel::getName, Function.identity()));
            Map<String, DataCategory> dataCategories = dataCategoryRepo.findAll().stream().collect(Collectors.toMap(DataCategory::getName, Function.identity()));
            Map<String, Staff> wikiStaffs = staffRepo.findAll().stream().collect(Collectors.toMap(Staff::getUsername, Function.identity()));
            List<String> cdeKeyList = cdeRepo.findAll().stream().map(d -> d.getReportingSystem() + "__" + d.getTableName() + "__" + d.getColumnName()).collect(Collectors.toList());

            ExcelUtils.writeCell((XSSFSheet) workbook.getSheetAt(0), rowHeader.get(), listHeaders.size(), "Kết quả", 0, true, 0);

            if (listHeaders.size() != 23) {
                typeResponse.set(Constants.FILE_IMPORT_INVALID);
                ActionAuditRequest actionAuditRequest = new ActionAuditRequest(httpServletRequest, httpServletResponse, null, IMPORT_CDE, "01", ActionCode.READ, this.getClass().getName(), String.valueOf(stackTraceElement.getLineNumber()), stackTraceElement.getMethodName(), 0, "void", file, startTime, new Timestamp(System.currentTimeMillis()), 501L, FILE_INVALID, userName);
                log.info("actionAuditRequest2: {}", actionAuditRequest);
                return;
            }

            for (String headers : listHeaders) {
                if (!listHeadersTemplate.contains(headers)) {
                    typeResponse.set(Constants.FILE_IMPORT_INVALID);
                    ActionAuditRequest actionAuditRequest = new ActionAuditRequest(httpServletRequest, httpServletResponse, null, IMPORT_CDE, "01", ActionCode.READ, this.getClass().getName(), String.valueOf(stackTraceElement.getLineNumber()), stackTraceElement.getMethodName(), 0, "void", file, startTime, new Timestamp(System.currentTimeMillis()), 502L, FILE_INVALID, userName);
                    log.info("actionAuditRequest3: {}", actionAuditRequest);
                    return;
                }
            }

            if (listHeadersTemplate.size() != listHeaders.size()) {
                typeResponse.set(Constants.FILE_IMPORT_INVALID);
                ActionAuditRequest actionAuditRequest = new ActionAuditRequest(httpServletRequest, httpServletResponse, null, IMPORT_CDE, "01", ActionCode.READ, this.getClass().getName(), String.valueOf(stackTraceElement.getLineNumber()), stackTraceElement.getMethodName(), 0, "void", file, startTime, new Timestamp(System.currentTimeMillis()), 503L, FILE_INVALID, userName);
                log.info("actionAuditRequest4: {}", actionAuditRequest);
                return;
            }

            boolean checkConflict = true;
            int countRecord = 0;
            int countExist = 0;

            List<String> checkDuplicateDataImport = new ArrayList<>();
            for (int i = 0; i <= workbook.getSheetAt(0).getLastRowNum(); i++) {
                StringBuilder validateString = new StringBuilder();
                if (workbook.getSheetAt(0).getRow(i) == null || i <= rowHeader.get() || !isRowNotEmpty(workbook.getSheetAt(0).getRow(i))) {
                    continue;
                }
                int errCount = 0;
                String itSystemString = "";
                String tableNameString = "";
                String columnNameString = "";
                for (int j = 0; j < listHeadersTemplate.size(); j++) {
                    if (j == 23 || j == 0) continue;
                    String data;
                    Cell cell = workbook.getSheetAt(0).getRow(i).getCell(j);
                    if (cell == null) {
                        data = "";
                    } else if (cell.getCellType() == Cell.CELL_TYPE_ERROR) {
                        data = cell.toString();
                    } else if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
                        data = "";
                    } else {
                        data = ExcelUtils.formatCellValue(cell).trim();
                    }
                    if (StringUtils.isBlank(data) && (j == 1 || j == 2 || j == 3 || j == 5 || j == 6 || j == 7)) {
                        validateString.append(listHeaders.get(j).replace("\n", " (")).append(")").append(" không được để trống\n");
                        errCount++;
                    } else {
                        switch (j) {
                            case 0:
                                if ((data != null) && (!StringUtils.isBlank(data)) && !CommonUtils.isNumber(data)) {
                                    validateString.append(listHeaders.get(j).replace("\n", " (")).append(") ").append(MUST_NUMBER);
                                    errCount++;
                                }
                                break;
                            case 3:
                                if (!systems.containsKey(data)) {
                                    validateString.append(listHeaders.get(j).replace("\n", " (")).append(") ").append(NOT_BELONG_LIST);
                                    errCount++;
                                } else {
                                    itSystemString = data;
                                }
                                break;
                            case 4:
                                if (!data.isEmpty() && !databases.containsKey(data)) {
                                    validateString.append(listHeaders.get(j).replace("\n", " (")).append(") ").append(NOT_BELONG_LIST);
                                    errCount++;
                                }
                                break;
                            case 5:
                                tableNameString = data;
                                break;
                            case 6:
                                columnNameString = data;
                                break;
                            case 7:
                                if (!owners.containsKey(data)) {
                                    validateString.append(listHeaders.get(j).replace("\n", " (")).append(") ").append(NOT_BELONG_LIST);
                                    errCount++;
                                }
                                break;
                            case 8:
                                if (!data.isEmpty() && !owners.containsKey(data)) {
                                    validateString.append(listHeaders.get(j).replace("\n", ")") + ") " + NOT_BELONG_LIST);
                                    errCount++;
                                }
                                break;
                            case 9:
                                if (!data.isEmpty() && !owners.containsKey(data)) {
                                    validateString.append(listHeaders.get(j).replace("\n", ")") + ") " + NOT_BELONG_LIST);
                                    errCount++;
                                }
                                break;
                            case 10:
                                if (!data.isEmpty() && !dataGroups.containsKey(data)) {
                                    validateString.append(listHeaders.get(j).replace("\n", ")") + ")" + NOT_BELONG_LIST);
                                    errCount++;
                                }
                                break;
                            case 11:
                                if (!data.isEmpty() && !businessProcesses.containsKey(data)) {
                                    validateString.append(listHeaders.get(j).replace("\n", " (") + ")" + NOT_BELONG_LIST);
                                    errCount++;
                                }
                                break;
                            case 12:
                                if (!data.isEmpty() && !reports.containsKey(data)) {
                                    validateString.append(listHeaders.get(j).replace("\n", ")") + ")" + NOT_BELONG_LIST);
                                    errCount++;
                                }
                                break;
                            case 13:
                                if (!data.isEmpty() && !wikiStaffs.containsKey(data)) {
                                    validateString.append(listHeaders.get(j).replace("\n", ")") + ")" + NOT_BELONG_LIST);
                                    errCount++;
                                }
                                break;
                            case 14:
                                if (!data.isEmpty() && !wikiStaffs.containsKey(data)) {
                                    validateString.append(listHeaders.get(j).replace("\n", " (") + ")" + NOT_BELONG_LIST);
                                    errCount++;
                                }
                                break;
                            case 15:
                                if (!data.isEmpty() && !wikiStaffs.containsKey(data)) {
                                    validateString.append(listHeaders.get(j).replace("\n", " (") + ")" + NOT_BELONG_LIST);
                                    errCount++;
                                }
                                break;
                            case 16:
                                if (!data.isEmpty() && ExcelUtils.convertToDateCatchException(data) == null) {
                                    validateString.append(listHeaders.get(j).replace("\n", " (") + ")" + " không đúng định dạng\n");
                                    errCount++;
                                }
                                break;
                            case 17:
                                if (!data.isEmpty() && !cdeTypes.containsKey(data)) {
                                    validateString.append(listHeaders.get(j).replace("\n", ") ") + ")" + NOT_BELONG_LIST);
                                    errCount++;
                                }
                                break;
                            case 18:
                                if ((data != null) && (!StringUtils.isBlank(data)) && (!"0".equals(data) && !"1".equals(data))) {
                                    validateString.append(listHeaders.get(j).replace("\n", ") ") + ")" + " phải chọn giá trị 0 hoặc 1\n");
                                    errCount++;
                                }
                                break;
                            case 19:
                                if (!data.isEmpty() && !confidentialLevels.containsKey(data)) {
                                    validateString.append(listHeaders.get(j).replace("\n", " (") + ")" + NOT_BELONG_LIST);
                                    errCount++;
                                }
                                break;
                            case 20:
                                if (!data.isEmpty() && !dataCategories.containsKey(data)) {
                                    validateString.append(listHeaders.get(j).replace("\n", ") ") + ")" + NOT_BELONG_LIST);
                                    errCount++;
                                }
                                break;
                            case 21:
                                if ((data != null) && (!StringUtils.isBlank(data)) && !CommonUtils.isNumber(data)) {
                                    validateString.append(listHeaders.get(j).replace("\n", " (") + ")" + MUST_NUMBER);
                                    errCount++;
                                }
                                break;
                            case 22:
                                if ((data != null) && (!StringUtils.isBlank(data)) && !CommonUtils.isNumber(data)) {
                                    validateString.append(listHeaders.get(j).replace("\n", ") ") + ")" + MUST_NUMBER);
                                    errCount++;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
                if (!itSystemString.isBlank() && !tableNameString.isBlank() && !columnNameString.isBlank()) {
                    if (checkDuplicateDataImport.contains(systems.get(itSystemString).getId() + "__" + tableNameString + "__" + columnNameString)) {
                        validateString.append("Không được import data các trường Hệ thống báo cáo, tên bảng, tên cột trùng nhau\n");
                        errCount++;
                    } else {
                        checkDuplicateDataImport.add(systems.get(itSystemString).getId() + "__" + tableNameString + "__" + columnNameString);
                    }
                }
                if (errCount > 0) {
                    checkConflict = false;
                    ExcelUtils.writeCell((XSSFSheet) workbook.getSheetAt(0), i, listHeadersTemplate.size(), validateString.toString(), 0, false, 0);
                    typeResponse.set(Constants.FILE_IMPORT_ERROR);
                } else {
                    if (!itSystemString.isBlank() && !tableNameString.isBlank() && !columnNameString.isBlank() && cdeKeyList.contains(systems.get(itSystemString).getId() + "__" + tableNameString + "__" + columnNameString)) {
                        countExist++;
                        ExcelUtils.writeCell((XSSFSheet) workbook.getSheetAt(0), i, listHeadersTemplate.size(), "Tồn tại dữ liệu trọng yếu trùng hệ thống báo cáo, tên bảng, tên cột trong cơ sở dữ liệu\n", 0, false, 0);
                    }
                    checkData.set(true);
                }
                countRecord++;
            }

            if (countRecord == 0) {
                typeResponse.set(Constants.FILE_IMPORT_INVALID);
                ActionAuditRequest actionAuditRequest = new ActionAuditRequest(httpServletRequest, httpServletResponse, null, IMPORT_CDE, "01", ActionCode.READ, this.getClass().getName(), String.valueOf(stackTraceElement.getLineNumber()), stackTraceElement.getMethodName(), countRecord, "void", file, startTime, new Timestamp(System.currentTimeMillis()), 504L, FILE_INVALID, userName);
                log.info("actionAuditRequest5: {}", actionAuditRequest);
                return;
            }

            if (!checkConflict) {
                typeResponse.set(Constants.FILE_IMPORT_ERROR);
                ActionAuditRequest actionAuditRequest = new ActionAuditRequest(httpServletRequest, httpServletResponse, null, IMPORT_CDE, "01", ActionCode.READ, this.getClass().getName(), String.valueOf(stackTraceElement.getLineNumber()), stackTraceElement.getMethodName(), countRecord, "void", file, startTime, new Timestamp(System.currentTimeMillis()), 418L, "Data error", userName);
                log.info("actionAuditRequest6: {}", actionAuditRequest);
            } else if (countExist > 0) {
                validateFileResponse.setNumberExist(countExist);
                typeResponse.set(Constants.FILE_IMPORT_CONFLICT);
                ActionAuditRequest actionAuditRequest = new ActionAuditRequest(httpServletRequest, httpServletResponse, null, IMPORT_CDE, "01", ActionCode.READ, this.getClass().getName(), String.valueOf(stackTraceElement.getLineNumber()), stackTraceElement.getMethodName(), countRecord, "void", file, startTime, new Timestamp(System.currentTimeMillis()), 409L, "Data exist", userName);
                log.info("actionAuditRequest7: {}", actionAuditRequest);
            } else if (typeResponse.get() == Constants.FILE_IMPORT_SUCCESS) {
                typeResponse.set(Constants.FILE_IMPORT_SUCCESS);
                ActionAuditRequest actionAuditRequest = new ActionAuditRequest(httpServletRequest, httpServletResponse, null, IMPORT_CDE, "01", ActionCode.READ, this.getClass().getName(), String.valueOf(stackTraceElement.getLineNumber()), stackTraceElement.getMethodName(), countRecord, "void", file, startTime, new Timestamp(System.currentTimeMillis()), userName);
                log.info("actionAuditRequest8: {}", actionAuditRequest);
            }
            validateFileResponse.setFileResponse(CommonUtils.uploadFileToFolder(workbook));
        } catch (IOException ioException) {
            ioException.printStackTrace();
            typeResponse.set(Constants.FILE_IMPORT_INVALID);
        }
    }

    private static boolean isRowNotEmpty(Row row) {
        if (row == null) {
            return false;
        }
        for (int cellNum = row.getFirstCellNum(); cellNum <= row.getLastCellNum(); cellNum++) {
            if (row.getCell(cellNum) != null && !row.getCell(cellNum).toString().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ByteArrayResource importFile(String name, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        //set data log
        Timestamp startTime = new java.sql.Timestamp(System.currentTimeMillis());
        StackTraceElement stackTraceElement = new Throwable().getStackTrace()[0];
        String userName = Constants.LOGIN_DEFAULT;
//        UserLogin userLogin = getCurrentUser();
        try {
            Map<String, Department> owners = departmentRepo.findAll().stream().collect(Collectors.toMap(item -> item.getCode(), o -> o, (existing, replacement) -> existing));
            Map<String, Database> databases = databaseRepo.findAll().stream().collect(Collectors.toMap(Database::getName, Function.identity()));
            Map<String, BusinessProcess> businessProcesses = businessProcessRepo.findAll().stream().collect(Collectors.toMap(BusinessProcess::getName, Function.identity()));
            Map<String, DataGroup> dataGroups = dataGroupRepo.findAll().stream().collect(Collectors.toMap(DataGroup::getName, Function.identity()));
            Map<String, ItSystem> systems = itSystemRepo.findAll().stream().collect(Collectors.toMap(s -> s.getCode(), Function.identity()));
            Map<String, Report> reports = reportRepo.findAll().stream().collect(Collectors.toMap(Report::getName, Function.identity(), (existing, replacement) -> existing));
            Map<String, CdeType> cdeTypes = cdeTypeRepo.findAll().stream().collect(Collectors.toMap(CdeType::getName, Function.identity()));
            Map<String, ConfidentialLevel> confidentialLevels = confidentialLevelRepo.findAll().stream().collect(Collectors.toMap(ConfidentialLevel::getName, Function.identity()));
            Map<String, DataCategory> dataCategories = dataCategoryRepo.findAll().stream().collect(Collectors.toMap(DataCategory::getName, Function.identity()));
            Map<String, Staff> wikiStaffs = staffRepo.findAll().stream().collect(Collectors.toMap(Staff::getUsername, Function.identity()));
            Map<String, Cde> cdeMap = cdeRepo.findAll().stream().collect(Collectors.toMap(d -> d.getReportingSystem() + d.getTableName() + d.getColumnName(), Function.identity(), (existing, replacement) -> existing));
            File file = new File(System.getProperty("user.dir") + "/fileUpload/FileErrorImport/" + name); // Đọc tệp tin Excel vào đối tượng Workbook
            FileInputStream inputStream = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(inputStream);
            DataFormatter dataFormatter = new DataFormatter();
            List<Integer> listHeadersRequire = new ArrayList<>();
            List<String> listHeaders = new ArrayList<>();
            Workbook workbookTemplate = ExcelUtils.getWorkbook(TEMPLATE_PATH, true);
            List<String> listHeadersTemplate = new ArrayList<>();
            AtomicInteger rowHeader = new AtomicInteger(0);
            ExcelUtils.getListHeader(workbookTemplate, dataFormatter, new ArrayList<>(), listHeadersTemplate, new AtomicInteger(0));
            ExcelUtils.getListHeaderIndex(workbook, dataFormatter, listHeadersRequire, listHeaders, rowHeader);
            List<Cde> cdeList = new ArrayList<>();
            ExcelUtils.writeCell((XSSFSheet) workbook.getSheetAt(0), rowHeader.get(), listHeaders.size(), "Kết quả", 0, true, 0);
            Cde cde;
            for (int i = 0; i <= workbook.getSheetAt(0).getLastRowNum(); i++) {
                if (workbook.getSheetAt(0).getRow(i) == null || i <= rowHeader.get()) {
                    continue;
                }
                if (!isRowNotEmpty(workbook.getSheetAt(0).getRow(i))) continue;
                cde = new Cde();
                String rpSystem = systems.get(ExcelUtils.formatCellValue(workbook.getSheetAt(0).getRow(i).getCell(3)).trim()).getId().toString();
                String table = ExcelUtils.formatCellValue(workbook.getSheetAt(0).getRow(i).getCell(5)).trim();
                String column = ExcelUtils.formatCellValue(workbook.getSheetAt(0).getRow(i).getCell(6)).trim();
                if (cdeMap.containsKey(rpSystem + "__" + table + "__" + column)) {
                    cde = cdeMap.get(rpSystem + "__" + table + "__" + column);
                } else {
//                cde.setCreatedAt(new Date());
//                cde.setCreatedBy (userLogin.getId());
                }
//                cde.setUpdatedAt(new Date());
//                cde.setUpdatedBy (userLogin.getId());
                for (int j = 0; j < listHeadersTemplate.size(); j++) {
                    if (j == 23) break;
                    Cell cellImport = workbook.getSheetAt(0).getRow(i).getCell(j);
                    String data = ExcelUtils.formatCellValue(cellImport).trim();
                    switch (j) {
                        case 1:
                            cde.setTitle(data);
                            break;
                        case 2:
                            cde.setDescription(data);
                            break;
                        case 3:
                            if (systems.containsKey(data)) {
                                cde.setReportingSystem(systems.get(data).getId());
                            }
                            break;
                        case 4:
                            if (!data.isEmpty() && databases.containsKey(data)) {
                                cde.setDatabase(databases.get(data).getId());
                            } else {
                                cde.setDatabase(null);
                            }
                            break;
                        case 5:
                            cde.setTableName(data);
                            break;
                        case 6:
                            cde.setColumnName(data);
                            break;
                        case 7:
                            if (owners.containsKey(data)) {
                                cde.setDataOwner(owners.get(data).getId());
                            } else {
                                cde.setDataOwner(null);
                            }
                            break;
                        case 8:
                            if (!data.isEmpty() && owners.containsKey(data)) {
                                cde.setApplicationOwner(owners.get(data).getId());

                            } else {
                                cde.setApplicationOwner(null);
                            }
                            break;
                        case 9:
                            if (!data.isEmpty() && owners.containsKey(data)) {
                                cde.setSystemOwner(owners.get(data).getId());
                            } else {
                                cde.setSystemOwner(null);
                            }
                            break;
                        case 10:
                            if (!data.isEmpty() && dataGroups.containsKey(data)) {
                                cde.setDataDomain(dataGroups.get(data).getId());
                            } else {
                                cde.setDataDomain(null);
                            }
                            break;
                        case 11:
                            if (!data.isEmpty() && businessProcesses.containsKey(data)) {
                                cde.setBusinessProcess(businessProcesses.get(data).getId());
                            } else {
                                cde.setBusinessProcess(null);
                            }
                            break;
                        case 12:
                            if (!data.isEmpty() && reports.containsKey(data)) {
                                cde.setReport(reports.get(data).getId());
                            } else {
                                cde.setReport(null);
                            }
                            break;
                        case 13:
                            if (!data.isEmpty() && wikiStaffs.containsKey(data)) {
                                cde.setBusinessDataSteward(wikiStaffs.get(data).getId());
                            } else {
                                cde.setBusinessDataSteward(null);
                            }
                            break;
                        case 14:
                            if (!data.isEmpty() && wikiStaffs.containsKey(data)) {
                                cde.setTechnicalDataSteward(wikiStaffs.get(data).getId());
                            } else {
                                cde.setTechnicalDataSteward(null);
                            }
                            break;
                        case 15:
                            if (!data.isEmpty() && wikiStaffs.containsKey(data)) {
                                cde.setOperationalDataSteward(wikiStaffs.get(data).getId());
                            } else {
                                cde.setOperationalDataSteward(null);
                            }
                            break;
                        case 16:
                            if (!data.isEmpty()) {
                                cde.setPublishedAt(ExcelUtils.convertToDate(data));
                            } else {
                                cde.setPublishedAt(null);
                            }
                            break;
                        case 17:
                            if (!data.isEmpty() && cdeTypes.containsKey(data)) {
                                cde.setCdeType(cdeTypes.get(data).getId());
                            } else {
                                cde.setCdeType(null);
                            }
                            break;
                        case 18:
                            if ((data != null) && (!StringUtils.isBlank(data)) && CommonUtils.isNumber2(data)) {
                                cde.setSensitiveData(Long.parseLong(data));
                            } else {
                                cde.setSensitiveData(null);
                            }
                            break;
                        case 19:
                            if (!data.isEmpty() && confidentialLevels.containsKey(data)) {
                                cde.setConfidentialLevel(confidentialLevels.get(data).getId());
                            } else {
                                cde.setConfidentialLevel(null);
                            }
                            break;
                        case 20:
                            if (!data.isEmpty() && dataCategories.containsKey(data)) {
                                cde.setDataCategory(dataCategories.get(data).getId());
                            } else {
                                cde.setDataCategory(null);
                            }
                            break;
                        case 21:
                            if ((data != null) && (!StringUtils.isBlank(data)) && CommonUtils.isNumber(data)) {
                                cde.setMaskedField(Long.parseLong(data));
                            } else {
                                cde.setMaskedField(null);
                            }
                            break;
                        case 22:
                            if (!data.isEmpty() && CommonUtils.isNumber(data)) {
                                cde.setRuleType(Long.parseLong(data));
                            } else {
                                cde.setRuleType(null);
                            }
                            break;
                        default:
                            break;
                    }
                }
//            cde.setUpdateBySystem(SYSTEM);
//            cde.setCreateBySystem(SYSTEM);
                cdeList.add(cde);
            }
            cdeRepo.saveAll(cdeList);
            ActionAuditRequest actionAuditRequest = new ActionAuditRequest(httpServletRequest, httpServletResponse, cdeList, IMPORT_CDE, "01", ActionCode.CREATE, this.getClass().getName(), String.valueOf(stackTraceElement.getLineNumber()), stackTraceElement.getMethodName(), cdeList.size(), "ByteArrayResource", name, startTime, new java.sql.Timestamp(System.currentTimeMillis()), userName);
            log.info("actionAuditRequest9: {}", actionAuditRequest);
            return null;
        } catch (Exception exception) {
            exception.printStackTrace();
            ActionAuditRequest actionAuditRequest = new ActionAuditRequest(httpServletRequest, httpServletResponse, null, IMPORT_CDE, "01", ActionCode.CREATE, this.getClass().getName(), String.valueOf(stackTraceElement.getLineNumber()), stackTraceElement.getMethodName(), 0, "ByteArrayResource", name, startTime, new java.sql.Timestamp(System.currentTimeMillis()), 505L, FILE_INVALID, userName);
            log.info("actionAuditRequest10: {}", actionAuditRequest);
            return new ByteArrayResource(Constants.FILE_INVALID_IMPORT);
        }
    }

    @Override
    public ByteArrayResource getFile(String nameFile, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        return ExcelUtils.downloadTemplateV2(System.getProperty("user.dir") + "/fileUpload/FileErrorImport/" + nameFile);
    }

    @Override
    public ByteArrayResource export(CdeRequest request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws JsonProcessingException {
        List<CdeExportExcel> excelFileExportDTOS = new ArrayList<>();
        AtomicInteger index = new AtomicInteger();
        request.setPage(null);
        request.setPageSize(null);
        Page<CdeResponse> cdeResponseDtoList = cdeRepo.searchCde(request);
//        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cdeResponseDtoList.getContent()));
        cdeResponseDtoList.forEach(item -> {
            index.getAndIncrement();
            excelFileExportDTOS.add(convertCdeToExcel(item, index.get()));
        });
        SheetData<CdeExportExcel> sheetData = new SheetData<>(3, "Thông tin CDE", excelFileExportDTOS, CdeExportExcel.class);
        return ExcelUtils.exportLargeDataChecklist(Collections.singletonList(sheetData), TEMPLATE_PATH_EXPORT);
    }

    private CdeExportExcel convertCdeToExcel(CdeResponse cde, Integer index) {
        CdeExportExcel excelFileExportDTO = mapper.convertValue(cde, CdeExportExcel.class);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        excelFileExportDTO.setPublishedAt(sdf.format(cde.getPublishedAt()));
        excelFileExportDTO.setIndex(String.valueOf(index));
        return excelFileExportDTO;
    }

}

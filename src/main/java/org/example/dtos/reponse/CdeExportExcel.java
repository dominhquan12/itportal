package org.example.dtos.reponse;


import lombok.Data;
import org.example.utils.annotation.ExcelColumn;

import java.util.Date;

@Data
public class CdeExportExcel extends ImportInputExcel {
    @ExcelColumn(col = 0)
    private String index;
    @ExcelColumn(col = 1)
    private String title;
    @ExcelColumn(col = 2)
    private String description;
    @ExcelColumn(col = 3)
    private String reportingSystem;
    @ExcelColumn(col = 4)
    private String database;
    @ExcelColumn(col = 5)
    private String tableName;
    @ExcelColumn(col = 6)
    private String columnName;
    @ExcelColumn(col = 7)
    private String dataOwner;
    @ExcelColumn(col = 8)
    private String applicationOwner;
    @ExcelColumn(col = 9)
    private String systemOwner;
    @ExcelColumn(col = 10)
    private String dataDomain;
    @ExcelColumn(col = 11)
    private String businessProcess;
    @ExcelColumn(col = 12)
    private String report;
    @ExcelColumn(col = 13)
    private String businessDataSteward;
    @ExcelColumn(col = 14)
    private String technicalDataSteward;
    @ExcelColumn(col =15)
    private String operationalDataSteward;
    @ExcelColumn(col =16)
    private String publishedAt;
    @ExcelColumn(col = 17)
    private String cdeType;
    @ExcelColumn(col = 18)
    private String sensitiveData;
    @ExcelColumn(col = 19)
    private String confidentialLevel;
    @ExcelColumn(col = 20) private String dataCategory;
    @ExcelColumn(col = 21)
    private String maskedField;
    @ExcelColumn(col = 22)
    private String ruleType;
}
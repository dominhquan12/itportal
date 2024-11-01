package org.example.dtos.reponse;

import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.entities.*;

import java.util.Date;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CdeResponse {
    Long id;
    String title;
    String description;
    String reportingSystem;
    String database;
    String tableName;
    String columnName;
    String dataOwner;
    String systemOwner;
    String applicationOwner;
    String dataDomain;
    String businessProcess;
    String report;
    String businessDataSteward;
    String technicalDataSteward;
    String operationalDataSteward;
    Date publishedAt;
    String cdeType;
    String sensitiveData;
    String confidentialLevel;
    String dataCategory;
    String maskedField;
    String ruleType;
}
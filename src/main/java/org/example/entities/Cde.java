package org.example.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.example.dtos.reponse.CdeResponse;

import java.util.Date;

@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "critical_data_element")

@SqlResultSetMapping(
        name = "searchCde",
        classes = {
                @ConstructorResult(
                        targetClass = CdeResponse.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "title", type = String.class),
                                @ColumnResult(name = "description", type = String.class),
                                @ColumnResult(name = "reportingSystem", type = String.class),
                                @ColumnResult(name = "database", type = String.class),
                                @ColumnResult(name = "tableName", type = String.class),
                                @ColumnResult(name = "columnName", type = String.class),
                                @ColumnResult(name = "dataOwner", type = String.class),
                                @ColumnResult(name = "systemOwner", type = String.class),
                                @ColumnResult(name = "applicationOwner", type = String.class),
                                @ColumnResult(name = "dataDomain", type = String.class),
                                @ColumnResult(name = "businessProcess", type = String.class),
                                @ColumnResult(name = "report", type = String.class),
                                @ColumnResult(name = "businessDataSteward", type = String.class),
                                @ColumnResult(name = "technicalDataSteward", type = String.class),
                                @ColumnResult(name = "operationalDataSteward", type = String.class),
                                @ColumnResult(name = "publishedAt", type = Date.class),
                                @ColumnResult(name = "cdeType", type = String.class),
                                @ColumnResult(name = "sensitiveData", type = String.class),
                                @ColumnResult(name = "confidentialLevel", type = String.class),
                                @ColumnResult(name = "dataCategory", type = String.class),
                                @ColumnResult(name = "maskedField", type = String.class),
                                @ColumnResult(name = "ruleType", type = String.class),
                        }
                )
        }
)

public class Cde {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String title;
    String description;
    Long reportingSystem;
    @Column(name = "db")
    Long database;
    String tableName;
    String columnName;
    Long dataOwner;
    Long systemOwner;
    Long applicationOwner;
    Long dataDomain;
    Long businessProcess;
    Long report;
    Long businessDataSteward;
    Long technicalDataSteward;
    Long operationalDataSteward;
    Date publishedAt;
    Long cdeType;
    Long sensitiveData;
    Long confidentialLevel;
    Long dataCategory;
    Long maskedField;
    Long ruleType;
}

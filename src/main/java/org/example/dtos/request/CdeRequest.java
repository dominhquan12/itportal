package org.example.dtos.request;


import lombok.Data;

import java.util.List;

@Data
public class CdeRequest {
    private Integer page = 0;
    private Integer pageSize = 10;
    private String sortBy;
    private String sortType;
    private String title;
    private String tableName;
    private String columnName;
    private Long dataGroupId;
    private Long dataOwnerId;
    private Long applicationOwnerId;
    private Long businessProcessId;
    private Long systemId;
    private Long databaseId;
    private List<Long> ids;
}
package org.example.repositories.custom.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.example.dtos.reponse.CdeResponse;
import org.example.dtos.request.CdeRequest;
import org.example.entities.Cde;
import org.example.repositories.custom.CdeRepoCustom;
import org.example.utils.DataUtils;
import org.springframework.data.domain.*;

import java.util.*;

public class CdeRepoCustomImpl implements CdeRepoCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Page<CdeResponse> searchCde(CdeRequest request) {
        Query queryCount = buildQuerySearchCde(request, null);
        Long countResult = DataUtils.safeToLong(queryCount.getSingleResult());
        List<CdeResponse> result = new ArrayList<>();
        if (countResult == 0) {
            return new PageImpl<>(result, PageRequest.of(0, 50), 0);
        } else {
            Pageable pageable;
            if (Objects.isNull(request.getPage()) || Objects.isNull(request.getPageSize())) {
                pageable = PageRequest.of(0, countResult.intValue(), Sort.by("cde.title").ascending());
            } else {
                pageable = PageRequest.of(request.getPage(), request.getPageSize());
            }
            Query queryData = buildQuerySearchCde(request, pageable);
            result = queryData.getResultList();
            return new PageImpl<>(result, pageable, countResult);
        }
    }

    public Query buildQuerySearchCde(CdeRequest request, Pageable pageable) {
        StringBuilder sql = new StringBuilder();
        String sqlFinal;
        Map<String, Object> params = new HashMap<>();
        Query nativeQuery;
        sql.append("SELECT cde.id AS id, cde.title AS title, cde.description AS description, its.name AS reportingSystem,")
                .append("db.name AS `database`, cde.table_name AS tableName, cde.column_name as columnName, do.name AS dataOwner,")
                .append("so.name AS systemOwner, ao.name AS applicationOwner, dg.name as dataDomain, bp.name AS businessProcess,")
                .append("rp.name AS report, bds.name AS businessDataSteward, tds.name AS technicalDataSteward,")
                .append("ods.name AS operationalDataSteward, cde.published_at AS publishedAt, ct.name AS cdeType,")
                .append("cde.sensitive_data AS sensitiveData, cl.name AS confidentialLevel, dc.name AS dataCategory,")
                .append("cde.masked_field AS maskedField, cde.rule_type AS ruleType")
                .append(" FROM critical_data_element cde ")
                .append(" LEFT JOIN business_process bp ON cde.business_process = bp.id ")
                .append(" LEFT JOIN cde_type ct ON cde.cde_type = ct.id ")
                .append(" LEFT JOIN confidential_level cl ON cde.confidential_level = cl.id ")
                .append(" LEFT JOIN db ON cde.db = db.id ")
                .append(" LEFT JOIN data_category dc ON cde.data_category = dc.id ")
                .append(" LEFT JOIN data_group dg ON cde.data_domain = dg.id ")
                .append(" LEFT JOIN department so ON cde.system_owner = so.id ")
                .append(" LEFT JOIN department do ON cde.data_owner = do.id ")
                .append(" LEFT JOIN department ao ON cde.application_owner = ao.id ")
                .append(" LEFT JOIN it_system its ON cde.reporting_system = its.id ")
                .append(" LEFT JOIN report rp ON cde.report = rp.id ")
                .append(" LEFT JOIN staff bds on cde.business_data_steward = bds.id ")
                .append(" LEFT JOIN staff tds on cde.technical_data_steward = tds.id ")
                .append(" LEFT JOIN staff ods on cde.operational_data_steward = ods.id ")
                .append(" WHERE 1=1 ");
        if (!DataUtils.isNullOrEmpty(request.getDataGroupId())) {
            sql.append("AND cde.data_domain =:dataGroupId ");
            params.put("dataGroupId", request.getDataGroupId());
        }
        if (!DataUtils.isNullOrEmpty(request.getDataOwnerId())) {
        }
        if (!DataUtils.isNullOrEmpty(request.getApplicationOwnerId())) {
            sql.append("AND cde.application_owner =:applicationOwnerId ");
            params.put("applicationOwnerId", request.getApplicationOwnerId());
        }
        if (!DataUtils.isNullOrEmpty(request.getBusinessProcessId())) {
            sql.append("AND cde.business_process =: businessProcessId ");
            params.put("businessProcessId", request.getBusinessProcessId());
        }
        if (!DataUtils.isNullOrEmpty(request.getSystemId())) {
            sql.append("AND cde.reporting_system = :systemId ");
            params.put("systemId", request.getSystemId());
        }
        if (!DataUtils.isNullOrEmpty(request.getDatabaseId())) {
            sql.append("AND cde.database =:databaseId ");
            params.put("databaseId", request.getDatabaseId());
        }
        if (StringUtils.isNotEmpty(request.getTitle())) {
            sql.append("AND cde.title LIKE :title ");
            params.put("title", DataUtils.makeLikeStr(request.getTitle()));
        }
        if (StringUtils.isNotEmpty(request.getTableName())) {
            sql.append("AND cde.table_name LIKE tableName ");
            params.put("tableName", DataUtils.makeLikeStr(request.getTableName()));
        }
        if (StringUtils.isNotEmpty(request.getColumnName())) {
            sql.append("AND cde.column_name LIKE :columnName ");
            params.put("columnName", DataUtils.makeLikeStr(request.getColumnName()));
        }
        if (!DataUtils.isNullOrEmpty(request.getIds())) {
            sql.append("AND cde.id IN :ids ");
            params.put("ids", request.getIds());
        }
        if (pageable != null) {
            request.setSortBy(getTableAndColumnSort(request.getSortBy()));
            sql.append(" ORDER BY trim(").append(request.getSortBy()).append(") ").append(request.getSortType())
                    .append(" LIMIT ").append(pageable.getOffset()).append(",").append(pageable.getPageSize());
            sqlFinal = sql.toString();
            nativeQuery = entityManager.createNativeQuery(sqlFinal, "searchCde");

        } else {
            sqlFinal = "SELECT COUNT(*) FROM (" + sql + ")_p \n";
            nativeQuery = entityManager.createNativeQuery(sqlFinal);
        }
        params.forEach(nativeQuery::setParameter);
        return nativeQuery;
    }

    private String getTableAndColumnSort(String sortBy) {
        switch (sortBy) {
            case "title":
                return "cde.title";
            case "businessProcess":
                return "bp.name";
            case "systemOwner":
                return "so.name";
            case "applicationOwner":
                return "ao.name";
            case "dataOwner":
                return "do.name";
            case "database":
                return "db.name";
            case "description":
                return "cde.description";
            case "tableName":
                return "cde.table_name";
            case "columnName":
                return "cde.column_name";
            default:
                return "cde.id";
        }
    }
}

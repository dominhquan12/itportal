package org.example.repositories.custom.impl;

import org.example.dtos.reponse.ProductResponse;
import org.example.dtos.reponse.ProductSearchResponse;
import org.example.dtos.request.ProductSearchRequest;
import org.example.repositories.custom.ProductRepoCustom;
import org.springframework.data.domain.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ProductRepoCustomImpl implements ProductRepoCustom {

    @PersistenceContext
    EntityManager em;

    @Override
    public Page<ProductSearchResponse> search(ProductSearchRequest request, Pageable pageable) {
        Query queryCount = buildQuerySearch(request, null);
        Object countResult = queryCount.getSingleResult();
        List<ProductSearchResponse> content = new ArrayList<>();
        Integer count = Integer.parseInt(countResult.toString());
        if (count > 0) {
            if(pageable == null) {
                pageable = PageRequest.of(0, count, Sort.by("name").descending());
            }
            Query query = buildQuerySearch(request, pageable);
            content = query.getResultList();
        } else {
            pageable = PageRequest.of(0, 1, Sort.by("id").ascending());
        }
        return new PageImpl<>(content, pageable, count);
    }

    Query buildQuerySearch(ProductSearchRequest request, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        String sqlFinal;
        Query query;

        sql.append("SELECT id, name, price FROM product p WHERE 1=1 ");
        if (request.getName() != null) {
            sql.append("AND name LIKE :name ");
            params.put("name", "%" + request.getName() + "%");
        }
        if (request.getPrice() != null) {
            sql.append("AND price = :price ");
            params.put("price", request.getPrice());
        }
        if (pageable != null) {
            Sort sort = pageable.getSort();
            if(!sort.isEmpty()) {
                sql.append(" ORDER BY ");
                sql.append(sort.stream().map(item -> item.getProperty() + " " + item.getDirection())
                        .collect(Collectors.joining(", ")));
            }
            query = em.createNativeQuery(sql.toString(), "productSearch");
            if(pageable.getPageSize() > 0 && pageable.getPageNumber() >= 0) {
                query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
                query.setMaxResults(pageable.getPageSize());
            }
        } else {
            sqlFinal = "SELECT COUNT(*) FROM (" + sql + ") c";
            query = em.createNativeQuery(sqlFinal);
        }
        params.forEach(query::setParameter);
        return query;
    }
}

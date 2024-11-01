package org.example.repositories.custom;

import org.example.dtos.reponse.ProductSearchResponse;
import org.example.dtos.request.ProductSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepoCustom {
    Page<ProductSearchResponse> search(ProductSearchRequest request, Pageable pageable);
}

package org.example.services;

import org.example.dtos.reponse.ProductResponse;
import org.example.dtos.reponse.ProductSearchResponse;
import org.example.dtos.request.ProductRequest;
import org.example.dtos.request.ProductSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponse create(ProductRequest request);
    Page<ProductSearchResponse> search(ProductSearchRequest request, Pageable pageable);

}

package org.example.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.dtos.reponse.ProductResponse;
import org.example.dtos.reponse.ProductSearchResponse;
import org.example.dtos.request.ProductRequest;
import org.example.dtos.request.ProductSearchRequest;
import org.example.entities.Product;
import org.example.repositories.ProductRepo;
import org.example.services.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductServiceImpl implements ProductService {

    ObjectMapper mapper;
    ProductRepo productRepo;

    @Override
    public ProductResponse create(ProductRequest request) {
        Product product = mapper.convertValue(request, Product.class);
        Product productCreated = productRepo.save(product);
        return mapper.convertValue(productCreated, ProductResponse.class);
    }

    @Override
    public Page<ProductSearchResponse> search(ProductSearchRequest request, Pageable pageable) {
        Page<ProductSearchResponse> response = productRepo.search(request, pageable);
        List<ProductSearchResponse> products = response.stream().collect(Collectors.toList());
        log.info("products: {}", products);
        return response;
    }

}
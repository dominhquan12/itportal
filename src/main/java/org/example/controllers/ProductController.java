package org.example.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.dtos.request.ProductRequest;
import org.example.dtos.request.ProductSearchRequest;
import org.example.services.ProductService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/products")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProductController {

    ProductService productService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.create(request));
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody ProductSearchRequest request, Pageable pageable) {
        return ResponseEntity.ok(productService.search(request, pageable));
    }


}

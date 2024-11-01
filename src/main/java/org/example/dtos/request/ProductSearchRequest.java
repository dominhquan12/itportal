package org.example.dtos.request;

import lombok.Data;

@Data
public class ProductSearchRequest {
    private String name;
    private Double price;
}

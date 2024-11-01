package org.example.dtos.request;

import lombok.Data;

@Data
public class ProductRequest {
    private Long id;
    private String name;
    private Double price;
}

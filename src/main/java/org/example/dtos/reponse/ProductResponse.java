package org.example.dtos.reponse;

import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private Double price;
}

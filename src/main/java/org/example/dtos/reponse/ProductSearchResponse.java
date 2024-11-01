package org.example.dtos.reponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchResponse {
    private Long id;
    private String name;
    private Double price;
}

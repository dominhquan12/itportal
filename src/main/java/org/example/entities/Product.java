package org.example.entities;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@SqlResultSetMapping(
        name = "productSearch",
        classes = {
                @ConstructorResult(
                        targetClass = org.example.dtos.reponse.ProductSearchResponse.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "name", type = String.class),
                                @ColumnResult(name = "price", type = Double.class)
                        }
                )
        }
)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double price;
}

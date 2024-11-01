package org.example.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.example.utils.annotation.ReferenceColumn;
@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "it_system")
public class ItSystem {
    @ReferenceColumn(value = "id", col = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ReferenceColumn(value = "name", col = 2)
    String name;
    @ReferenceColumn(value = "code", col = 3)
    String code;
}

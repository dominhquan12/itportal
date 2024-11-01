package org.example.repositories;

import org.example.entities.DataCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataCategoryRepo extends JpaRepository<DataCategory, Long> {
}

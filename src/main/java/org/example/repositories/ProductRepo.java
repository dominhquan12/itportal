package org.example.repositories;

import org.example.entities.Product;
import org.example.repositories.custom.ProductRepoCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, Long>, ProductRepoCustom {
}

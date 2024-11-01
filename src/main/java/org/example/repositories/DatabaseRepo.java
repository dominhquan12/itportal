package org.example.repositories;

import org.example.entities.Database;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatabaseRepo extends JpaRepository<Database, Long> {
}

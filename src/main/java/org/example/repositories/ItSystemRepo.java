package org.example.repositories;

import org.example.entities.ItSystem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItSystemRepo extends JpaRepository<ItSystem, Long> {
}

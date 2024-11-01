package org.example.repositories;

import org.example.entities.ConfidentialLevel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfidentialLevelRepo extends JpaRepository<ConfidentialLevel, Long> {
}

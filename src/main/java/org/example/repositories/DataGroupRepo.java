package org.example.repositories;

import org.example.entities.DataGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataGroupRepo extends JpaRepository<DataGroup, Long> {
}

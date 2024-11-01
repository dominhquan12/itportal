package org.example.repositories;

import org.example.entities.BusinessProcess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessProcessRepo extends JpaRepository<BusinessProcess, Long> {
}

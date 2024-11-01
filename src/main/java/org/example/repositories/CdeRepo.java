package org.example.repositories;

import org.example.entities.Cde;
import org.example.repositories.custom.CdeRepoCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CdeRepo extends JpaRepository<Cde, Long>, CdeRepoCustom {
}

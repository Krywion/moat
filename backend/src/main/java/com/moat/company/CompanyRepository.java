package com.moat.company;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    List<Company> findByOwnerId(UUID ownerId);

    Optional<Company> findByIdAndOwnerId(UUID id, UUID ownerId);
}

package com.example.basa_prof.repository;

import com.example.basa_prof.entity.Contractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractorRepository extends JpaRepository<Contractor, Long> {

    List<Contractor> findByOrganizationContainingIgnoreCase(String organization);

    List<Contractor> findAllByOrderByCreatedAtDesc();
}

package com.example.basa_prof.repository;

import com.example.basa_prof.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    List<Supplier> findByNameContainingIgnoreCase(String name);

    Optional<Supplier> findByEmail(String email);

    Optional<Supplier> findByPhone(String phone);
}

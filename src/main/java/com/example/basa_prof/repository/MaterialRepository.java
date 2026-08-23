package com.example.basa_prof.repository;

import com.example.basa_prof.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    List<Material> findByNameContainingIgnoreCase(String name);

    List<Material> findBySupplierId(Long supplierId);

    List<Material> findByObjectId(Long objectId);

    Optional<Material> findByUnit(String unit);
}

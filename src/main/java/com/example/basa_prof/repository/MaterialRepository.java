package com.example.basa_prof.repository;

import com.example.basa_prof.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    List<Material> findByNameContainingIgnoreCase(String name);

    List<Material> findBySupplierId(Long supplierId);

    List<Material> findByObjectId(Long objectId);

    Optional<Material> findByUnit(String unit);

    @Query("SELECT DISTINCT m FROM Material m LEFT JOIN FETCH m.supplier LEFT JOIN FETCH m.object o LEFT JOIN FETCH o.client LEFT JOIN FETCH m.deal")
    List<Material> findAllWithSupplierAndObject();

    @Query("SELECT DISTINCT m FROM Material m LEFT JOIN FETCH m.supplier LEFT JOIN FETCH m.object o LEFT JOIN FETCH o.client LEFT JOIN FETCH m.deal WHERE m.id = :id")
    Optional<Material> findByIdWithSupplierAndObject(Long id);
}

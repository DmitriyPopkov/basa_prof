package com.example.basa_prof.service;

import com.example.basa_prof.entity.Material;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с материалами.
 */
public interface IMaterialService {
    List<Material> findAll();
    Optional<Material> findById(Long id);
    Material save(Material material);
    void deleteById(Long id);
    List<Material> searchByName(String name);
    List<Material> findBySupplierId(Long supplierId);
    List<Material> findByObjectEntityId(Long objectEntityId);
    List<Material> findByDealId(Long dealId);
}

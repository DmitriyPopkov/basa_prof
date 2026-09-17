package com.example.basa_prof.service;

import com.example.basa_prof.entity.Supplier;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с поставщиками.
 */
public interface ISupplierService {
    List<Supplier> findAll();
    Optional<Supplier> findById(Long id);
    Supplier save(Supplier supplier);
    void deleteById(Long id);
}

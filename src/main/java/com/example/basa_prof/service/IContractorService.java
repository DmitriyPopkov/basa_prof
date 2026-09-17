package com.example.basa_prof.service;

import com.example.basa_prof.entity.Contractor;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с исполнителями.
 */
public interface IContractorService {
    List<Contractor> findAll();
    Optional<Contractor> findById(Long id);
    Contractor save(Contractor contractor);
    void deleteById(Long id);
}

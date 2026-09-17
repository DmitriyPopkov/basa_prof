package com.example.basa_prof.service;

import com.example.basa_prof.entity.Work;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с работами.
 */
public interface IWorkService {
    List<Work> findAll();
    Optional<Work> findById(Long id);
    Work save(Work work);
    void deleteById(Long id);
}

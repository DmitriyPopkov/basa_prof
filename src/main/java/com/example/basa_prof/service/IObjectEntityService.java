package com.example.basa_prof.service;

import com.example.basa_prof.entity.ObjectEntity;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с объектами.
 */
public interface IObjectEntityService {
    List<ObjectEntity> findAll();
    Optional<ObjectEntity> findById(Long id);
    ObjectEntity save(ObjectEntity object);
    void deleteById(Long id);
}

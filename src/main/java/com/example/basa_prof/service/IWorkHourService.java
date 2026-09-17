package com.example.basa_prof.service;

import com.example.basa_prof.entity.WorkHour;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с учётом рабочего времени.
 */
public interface IWorkHourService {
    List<WorkHour> findAll();
    Optional<WorkHour> findById(Long id);
    WorkHour save(WorkHour workHour);
    void deleteById(Long id);
}

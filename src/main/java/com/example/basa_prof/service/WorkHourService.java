package com.example.basa_prof.service;

import com.example.basa_prof.entity.WorkHour;
import com.example.basa_prof.repository.WorkHourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkHourService {

    @Autowired
    private WorkHourRepository workHourRepository;

    public List<WorkHour> findAll() {
        return workHourRepository.findAllWithRelations();
    }

    public Optional<WorkHour> findById(Long id) {
        return workHourRepository.findByIdWithRelations(id);
    }

    public WorkHour save(WorkHour workHour) {
        return workHourRepository.save(workHour);
    }

    public void deleteById(Long id) {
        workHourRepository.deleteById(id);
    }
}

package com.example.basa_prof.service;

import com.example.basa_prof.entity.Work;
import com.example.basa_prof.repository.WorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkService implements IWorkService {

    @Autowired
    private WorkRepository workRepository;

    public List<Work> findAll() {
        return workRepository.findAllWithObjectAndClient();
    }

    public Optional<Work> findById(Long id) {
        return workRepository.findByIdWithObjectAndClient(id);
    }

    public Work save(Work work) {
        return workRepository.save(work);
    }

    public void deleteById(Long id) {
        workRepository.deleteById(id);
    }
}

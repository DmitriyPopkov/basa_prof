package com.example.basa_prof.service;

import com.example.basa_prof.entity.Contractor;
import com.example.basa_prof.repository.ContractorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ContractorService implements IContractorService {

    @Autowired
    private ContractorRepository contractorRepository;

    public List<Contractor> findAll() {
        return contractorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Contractor> findById(Long id) {
        return contractorRepository.findById(id);
    }

    public Contractor save(Contractor contractor) {
        return contractorRepository.save(contractor);
    }

    public void deleteById(Long id) {
        contractorRepository.deleteById(id);
    }

    public List<Contractor> searchByName(String name) {
        return contractorRepository.findByOrganizationContainingIgnoreCase(name);
    }

    public List<Contractor> findAllOrderByDate() {
        return contractorRepository.findAllByOrderByCreatedAtDesc();
    }
}

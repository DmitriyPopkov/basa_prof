package com.example.basa_prof.service;

import com.example.basa_prof.entity.Material;
import com.example.basa_prof.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    public List<Material> findAll() {
        return materialRepository.findAllWithSupplierAndObject();
    }

    public Optional<Material> findById(Long id) {
        return materialRepository.findByIdWithSupplierAndObject(id);
    }

    public Material save(Material material) {
        return materialRepository.save(material);
    }

    public void deleteById(Long id) {
        materialRepository.deleteById(id);
    }

    public List<Material> searchByName(String name) {
        return materialRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Material> findBySupplierId(Long supplierId) {
        return materialRepository.findBySupplierId(supplierId);
    }

    public List<Material> findByObjectId(Long objectId) {
        return materialRepository.findByObjectId(objectId);
    }
}

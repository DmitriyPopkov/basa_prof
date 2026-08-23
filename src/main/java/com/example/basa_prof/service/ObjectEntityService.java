package com.example.basa_prof.service;

import com.example.basa_prof.entity.ObjectEntity;
import com.example.basa_prof.repository.ObjectEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ObjectEntityService {

    @Autowired
    private ObjectEntityRepository objectEntityRepository;

    public List<ObjectEntity> findAll() {
        return objectEntityRepository.findAll();
    }

    public Optional<ObjectEntity> findById(Long id) {
        return objectEntityRepository.findById(id);
    }

    public ObjectEntity save(ObjectEntity object) {
        return objectEntityRepository.save(object);
    }

    public void deleteById(Long id) {
        objectEntityRepository.deleteById(id);
    }

    public List<ObjectEntity> searchByName(String name) {
        return objectEntityRepository.findByNameContainingIgnoreCase(name);
    }

    public List<ObjectEntity> findByType(String objectType) {
        return objectEntityRepository.findByObjectType(objectType);
    }
}

package com.example.basa_prof.repository;

import com.example.basa_prof.entity.ObjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Repository
public interface ObjectEntityRepository extends JpaRepository<ObjectEntity, Long> {

    List<ObjectEntity> findByNameContainingIgnoreCase(String name);

    List<ObjectEntity> findByObjectType(String objectType);

    List<ObjectEntity> findAllByOrderByPriceDesc();
}

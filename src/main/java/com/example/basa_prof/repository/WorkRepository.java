package com.example.basa_prof.repository;

import com.example.basa_prof.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {

    List<Work> findByWorkType(String workType);

    List<Work> findAllByOrderByEstimatedCostDesc();
}

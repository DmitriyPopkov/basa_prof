package com.example.basa_prof.repository;

import com.example.basa_prof.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkRepository extends JpaRepository<Work, Long> {

    List<Work> findByWorkType(String workType);

    List<Work> findAllByOrderByEstimatedCostDesc();

    @Query("SELECT DISTINCT w FROM Work w LEFT JOIN FETCH w.object o LEFT JOIN FETCH o.client LEFT JOIN FETCH w.deal")
    List<Work> findAllWithObjectAndClient();

    @Query("SELECT DISTINCT w FROM Work w LEFT JOIN FETCH w.object o LEFT JOIN FETCH o.client LEFT JOIN FETCH w.deal WHERE w.id = :id")
    Optional<Work> findByIdWithObjectAndClient(Long id);
}

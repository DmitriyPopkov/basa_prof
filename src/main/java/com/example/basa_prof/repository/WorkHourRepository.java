package com.example.basa_prof.repository;

import com.example.basa_prof.entity.WorkHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkHourRepository extends JpaRepository<WorkHour, Long> {

    @Query("SELECT DISTINCT wh FROM WorkHour wh LEFT JOIN FETCH wh.employee LEFT JOIN FETCH wh.client LEFT JOIN FETCH wh.object o LEFT JOIN FETCH o.client objClient LEFT JOIN FETCH wh.deal")
    List<WorkHour> findAllWithRelations();

    @Query("SELECT DISTINCT wh FROM WorkHour wh LEFT JOIN FETCH wh.employee LEFT JOIN FETCH wh.client LEFT JOIN FETCH wh.object o LEFT JOIN FETCH o.client objClient LEFT JOIN FETCH wh.deal WHERE wh.id = :id")
    Optional<WorkHour> findByIdWithRelations(Long id);
}

package com.example.basa_prof.repository;

import com.example.basa_prof.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByFullNameContainingIgnoreCase(String fullName);

    Optional<Client> findByPhone(String phone);

    List<Client> findAllByOrderByCreatedAtDesc();

    @Query("SELECT DISTINCT c FROM Client c LEFT JOIN FETCH c.objects")
    List<Client> findAllWithObjects();
}

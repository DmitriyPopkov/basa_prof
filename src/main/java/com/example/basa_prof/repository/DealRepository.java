package com.example.basa_prof.repository;

import com.example.basa_prof.entity.Client;
import com.example.basa_prof.entity.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {

    List<Deal> findByClientId(Long clientId);

    List<Deal> findByStatus(String status);

    List<Deal> findByClientOrderByDealDateDesc(Client client);

    @Query("SELECT DISTINCT d FROM Deal d LEFT JOIN FETCH d.client LEFT JOIN FETCH d.contractor")
    List<Deal> findAllWithClientAndContractor();
}

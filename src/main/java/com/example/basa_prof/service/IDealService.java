package com.example.basa_prof.service;

import com.example.basa_prof.entity.Deal;
import com.example.basa_prof.entity.Client;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы со сделками.
 */
public interface IDealService {
    List<Deal> findAll();
    Optional<Deal> findById(Long id);
    Optional<Deal> findByIdWithObjects(Long id);
    Deal save(Deal deal);
    void deleteById(Long id);
    List<Deal> findByClientId(Long clientId);
    List<Deal> findByStatus(String status);
    List<Deal> findByClientOrderByDealDateDesc(Client client);
}

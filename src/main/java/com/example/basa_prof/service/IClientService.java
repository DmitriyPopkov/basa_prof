package com.example.basa_prof.service;

import com.example.basa_prof.entity.Client;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с клиентами.
 * Реализует принцип Dependency Inversion (D) и Interface Segregation (I).
 */
public interface IClientService {
    List<Client> findAll();
    Optional<Client> findById(Long id);
    Client save(Client client);
    void deleteById(Long id);
}

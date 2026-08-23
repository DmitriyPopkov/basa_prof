package com.example.basa_prof.service;

import com.example.basa_prof.entity.Client;
import com.example.basa_prof.entity.Deal;
import com.example.basa_prof.repository.DealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DealService {

    @Autowired
    private DealRepository dealRepository;

    public List<Deal> findAll() {
        return dealRepository.findAll();
    }

    public Optional<Deal> findById(Long id) {
        return dealRepository.findById(id);
    }

    public Deal save(Deal deal) {
        return dealRepository.save(deal);
    }

    public void deleteById(Long id) {
        dealRepository.deleteById(id);
    }

    public List<Deal> findByClientId(Long clientId) {
        return dealRepository.findByClientId(clientId);
    }

    public List<Deal> findByStatus(String status) {
        return dealRepository.findByStatus(status);
    }

    public List<Deal> findByClientOrderByDate(Client client) {
        return dealRepository.findByClientOrderByDealDateDesc(client);
    }
}

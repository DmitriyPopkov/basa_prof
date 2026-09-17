package com.example.basa_prof.service;

import com.example.basa_prof.entity.Client;
import com.example.basa_prof.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService implements IClientService {

    @Autowired
    private ClientRepository clientRepository;

    public List<Client> findAll() {
        return clientRepository.findAllWithObjects();
    }

    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    public Client save(Client client) {
        return clientRepository.save(client);
    }

    public void deleteById(Long id) {
        clientRepository.deleteById(id);
    }

    public List<Client> searchByName(String name) {
        return clientRepository.findByFullNameContainingIgnoreCase(name);
    }

    public Optional<Client> findByPhone(String phone) {
        return clientRepository.findByPhone(phone);
    }

    public List<Client> findAllOrderByDate() {
        return clientRepository.findAllByOrderByCreatedAtDesc();
    }
}

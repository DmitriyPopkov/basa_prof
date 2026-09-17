package com.example.basa_prof.service;

import com.example.basa_prof.entity.Client;
import com.example.basa_prof.entity.Deal;
import com.example.basa_prof.entity.Contractor;
import com.example.basa_prof.repository.DealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Optional;

@Service
public class DealService implements IDealService {

    @Autowired
    private DealRepository dealRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Deal> findAll() {
        return dealRepository.findAllWithClientAndContractor();
    }

    @Transactional(readOnly = true)
    public Optional<Deal> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Deal.class, id));
    }

    @Transactional(readOnly = true)
    public Optional<Deal> findByIdWithObjects(Long id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Deal> cq = cb.createQuery(Deal.class);
        Root<Deal> deal = cq.from(Deal.class);
        deal.fetch("objects", jakarta.persistence.criteria.JoinType.LEFT);
        cq.select(deal).where(cb.equal(deal.get("id"), id));
        return Optional.ofNullable(entityManager.createQuery(cq).getSingleResultOrNull());
    }

    @Transactional
    public Deal save(Deal deal) {
        if (deal.getId() == null) {
            entityManager.persist(deal);
        } else {
            entityManager.merge(deal);
        }
        return deal;
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

    @Override
    public List<Deal> findByClientOrderByDealDateDesc(Client client) {
        return dealRepository.findByClientOrderByDealDateDesc(client);
    }
}

package com.example.basa_prof.service;

import com.example.basa_prof.entity.Payment;
import com.example.basa_prof.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService implements IPaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public List<Payment> findAll() {
        return paymentRepository.findAllWithRelations();
    }

    @Transactional(readOnly = true)
    public Optional<Payment> findById(Long id) {
        return paymentRepository.findByIdWithRelations(id);
    }

    @Transactional
    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            paymentRepository.save(payment);
        } else {
            paymentRepository.save(payment);
        }
        return payment;
    }

    @Transactional
    public void deleteById(Long id) {
        paymentRepository.deleteById(id);
    }

    public List<Payment> findByClientId(Long clientId) {
        return paymentRepository.findByClientIdWithRelations(clientId);
    }

    public List<Payment> findByObjectId(Long objectId) {
        return paymentRepository.findByObjectIdWithRelations(objectId);
    }

    public List<Payment> findByClientIdAndObjectId(Long clientId, Long objectId) {
        return paymentRepository.findByClientIdAndObjectIdWithRelations(clientId, objectId);
    }

    public List<Payment> findByClientIdAndDealId(Long clientId, Long dealId) {
        return paymentRepository.findByClientIdAndDealIdWithRelations(clientId, dealId);
    }
}

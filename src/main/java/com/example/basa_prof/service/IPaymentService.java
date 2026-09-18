package com.example.basa_prof.service;

import com.example.basa_prof.entity.Payment;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с оплатами.
 */
public interface IPaymentService {
    List<Payment> findAll();
    Optional<Payment> findById(Long id);
    Payment save(Payment payment);
    void deleteById(Long id);
    List<Payment> findByClientId(Long clientId);
    List<Payment> findByObjectId(Long objectId);
    List<Payment> findByClientIdAndObjectId(Long clientId, Long objectId);
    List<Payment> findByClientIdAndDealId(Long clientId, Long dealId);
}

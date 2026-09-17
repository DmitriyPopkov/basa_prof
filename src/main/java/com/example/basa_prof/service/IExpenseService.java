package com.example.basa_prof.service;

import com.example.basa_prof.entity.Expense;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для работы с расходами.
 */
public interface IExpenseService {
    List<Expense> findAll();
    Optional<Expense> findById(Long id);
    Expense save(Expense expense);
    void deleteById(Long id);
    List<Expense> findByClientId(Long clientId);
    List<Expense> findByObjectId(Long objectId);
    List<Expense> findByClientIdAndObjectId(Long clientId, Long objectId);
    List<Expense> findByClientIdAndDealId(Long clientId, Long dealId);
}

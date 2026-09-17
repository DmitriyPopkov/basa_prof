package com.example.basa_prof.service;

import com.example.basa_prof.entity.Expense;
import com.example.basa_prof.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService implements IExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Transactional(readOnly = true)
    public List<Expense> findAll() {
        return expenseRepository.findAllWithRelations();
    }

    @Transactional(readOnly = true)
    public Optional<Expense> findById(Long id) {
        return expenseRepository.findByIdWithRelations(id);
    }

    @Transactional
    public Expense save(Expense expense) {
        if (expense.getId() == null) {
            expenseRepository.save(expense);
        } else {
            expenseRepository.save(expense);
        }
        return expense;
    }

    @Transactional
    public void deleteById(Long id) {
        expenseRepository.deleteById(id);
    }

    public List<Expense> findByClientId(Long clientId) {
        return expenseRepository.findByClientIdWithRelations(clientId);
    }

    public List<Expense> findByObjectId(Long objectId) {
        return expenseRepository.findByObjectIdWithRelations(objectId);
    }

    public List<Expense> findByClientIdAndObjectId(Long clientId, Long objectId) {
        return expenseRepository.findByClientIdAndObjectIdWithRelations(clientId, objectId);
    }

    public List<Expense> findByClientIdAndDealId(Long clientId, Long dealId) {
        return expenseRepository.findByClientIdAndDealIdWithRelations(clientId, dealId);
    }
}

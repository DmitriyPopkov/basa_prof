package com.example.basa_prof.repository;

import com.example.basa_prof.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT DISTINCT e FROM Expense e LEFT JOIN FETCH e.client LEFT JOIN FETCH e.objectEntity o LEFT JOIN FETCH o.client oClient LEFT JOIN FETCH e.deal")
    List<Expense> findAllWithRelations();

    @Query("SELECT DISTINCT e FROM Expense e LEFT JOIN FETCH e.client LEFT JOIN FETCH e.objectEntity LEFT JOIN FETCH e.deal WHERE e.id = :id")
    Optional<Expense> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT DISTINCT e FROM Expense e LEFT JOIN FETCH e.client LEFT JOIN FETCH e.objectEntity o LEFT JOIN FETCH o.client oClient LEFT JOIN FETCH e.deal WHERE e.client.id = :clientId OR (o IS NOT NULL AND o.client.id = :clientId)")
    List<Expense> findByClientIdWithRelations(@Param("clientId") Long clientId);

    @Query("SELECT DISTINCT e FROM Expense e LEFT JOIN FETCH e.client LEFT JOIN FETCH e.objectEntity o LEFT JOIN FETCH o.client oClient LEFT JOIN FETCH e.deal WHERE o.id = :objectId")
    List<Expense> findByObjectIdWithRelations(@Param("objectId") Long objectId);

    @Query("SELECT DISTINCT e FROM Expense e LEFT JOIN FETCH e.client LEFT JOIN FETCH e.objectEntity o LEFT JOIN FETCH o.client oClient LEFT JOIN FETCH e.deal WHERE (e.client.id = :clientId OR (o IS NOT NULL AND o.client.id = :clientId)) AND o.id = :objectId")
    List<Expense> findByClientIdAndObjectIdWithRelations(@Param("clientId") Long clientId, @Param("objectId") Long objectId);

    @Query("SELECT DISTINCT e FROM Expense e LEFT JOIN FETCH e.client LEFT JOIN FETCH e.objectEntity o LEFT JOIN FETCH o.client oClient LEFT JOIN FETCH e.deal WHERE (e.client.id = :clientId OR (o IS NOT NULL AND o.client.id = :clientId)) AND e.deal.id = :dealId")
    List<Expense> findByClientIdAndDealIdWithRelations(@Param("clientId") Long clientId, @Param("dealId") Long dealId);
}

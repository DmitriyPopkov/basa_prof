package com.example.basa_prof.repository;

import com.example.basa_prof.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT DISTINCT p FROM Payment p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.objectEntity o LEFT JOIN FETCH o.client oClient LEFT JOIN FETCH p.deal")
    List<Payment> findAllWithRelations();

    @Query("SELECT DISTINCT p FROM Payment p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.objectEntity LEFT JOIN FETCH p.deal WHERE p.id = :id")
    Optional<Payment> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Payment p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.objectEntity o LEFT JOIN FETCH o.client oClient LEFT JOIN FETCH p.deal WHERE p.client.id = :clientId OR (o IS NOT NULL AND o.client.id = :clientId)")
    List<Payment> findByClientIdWithRelations(@Param("clientId") Long clientId);

    @Query("SELECT DISTINCT p FROM Payment p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.objectEntity o LEFT JOIN FETCH o.client oClient LEFT JOIN FETCH p.deal WHERE o.id = :objectId")
    List<Payment> findByObjectIdWithRelations(@Param("objectId") Long objectId);

    @Query("SELECT DISTINCT p FROM Payment p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.objectEntity o LEFT JOIN FETCH o.client oClient LEFT JOIN FETCH p.deal WHERE (p.client.id = :clientId OR (o IS NOT NULL AND o.client.id = :clientId)) AND o.id = :objectId")
    List<Payment> findByClientIdAndObjectIdWithRelations(@Param("clientId") Long clientId, @Param("objectId") Long objectId);

    @Query("SELECT DISTINCT p FROM Payment p LEFT JOIN FETCH p.client LEFT JOIN FETCH p.objectEntity o LEFT JOIN FETCH o.client oClient LEFT JOIN FETCH p.deal WHERE (p.client.id = :clientId OR (o IS NOT NULL AND o.client.id = :clientId)) AND p.deal.id = :dealId")
    List<Payment> findByClientIdAndDealIdWithRelations(@Param("clientId") Long clientId, @Param("dealId") Long dealId);
}

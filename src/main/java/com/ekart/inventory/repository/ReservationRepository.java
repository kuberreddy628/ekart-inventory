package com.ekart.inventory.repository;

import com.ekart.inventory.entity.Reservations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservations, Long> {

    Optional<Reservations> findByOrderId(Long orderId);

    // Allow multiple reservation rows per order (one per product)
    java.util.List<Reservations> findAllByOrderId(Long orderId);
    }

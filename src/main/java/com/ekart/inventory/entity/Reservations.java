package com.ekart.inventory.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Reservations {


        @Id
        @GeneratedValue(strategy= GenerationType.IDENTITY)
        @Column(name = "reservationid")
        private Long reservationId;

        private Long orderId;
        private Long productId;
        private Long reservedQty;

        @Enumerated (EnumType.STRING)
        private ReservationStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getReservedQty() {
        return reservedQty;
    }

    public void setReservedQty(Long reservedQty) {
        this.reservedQty = reservedQty;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

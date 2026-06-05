package com.ekart.inventory.dto;

public class ReservationRequestDTO {

    private Long orderId;
    private Long productId;
    private Long reservedQty;   // change name to requestedQty

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
}

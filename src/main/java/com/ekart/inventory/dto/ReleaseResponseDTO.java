package com.ekart.inventory.dto;

import java.util.List;

public class ReleaseResponseDTO {

    private Long orderId;
    private String status;
    private List<ReleasedProductsDTO> releasedItems;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ReleasedProductsDTO> getReleasedItems() {
        return releasedItems;
    }

    public void setReleasedItems(List<ReleasedProductsDTO> releasedItems) {
        this.releasedItems = releasedItems;
    }
}

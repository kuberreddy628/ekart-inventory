package com.ekart.inventory.dto;

public class InventoryProductsResponseDTO {

    private Long productID;
    private String name;

    public Long getProductID() {
        return productID;
    }

    public void setProductID(Long productID) {
        this.productID = productID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

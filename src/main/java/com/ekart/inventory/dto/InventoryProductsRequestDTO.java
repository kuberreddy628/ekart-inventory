package com.ekart.inventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
public class InventoryProductsRequestDTO {

    private Long productID;

    @NotEmpty(message = "name is mandatory")
    private String name;

    @NotEmpty(message ="description is mandatory for products")
    private String description;

    @DecimalMin(value = "0.01", message ="price must be positive value")
    private BigDecimal price;

    @Min(value = 1, message ="quantity must be minimum 1")
    private Long availableQty;

    private Long requestedQty;


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(Long availableQty) {
        this.availableQty = availableQty;
    }

    public Long getRequestedQty() {
        return requestedQty;
    }

    public void setRequestedQty(Long requestedQty) {
        this.requestedQty = requestedQty;
    }
}

package com.ekart.inventory.dto;

import java.math.BigDecimal;

public class ProductDetailsResponseDTO {

    private Long productID;
    private String name;
    private String description;
    private BigDecimal price;
    private Long availableQty;                     // need to add customerId, quantity, address

   // private Long customerID;     //we dont need to add all these because we r just getting product details and sending back the product details, without these 3 fields
   // private Long quantity;
   // private AddressDTO addressDTO;    // no need to add because we created and using productDetailsResponseDTO class..

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
}

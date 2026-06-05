package com.ekart.inventory.dto;

public class ProductsResponseDTO {

    private Long productIO;
    private String name;

    public Long getProductIO() {
        return productIO;
    }

    public void setProductIO(Long productIO) {
        this.productIO = productIO;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

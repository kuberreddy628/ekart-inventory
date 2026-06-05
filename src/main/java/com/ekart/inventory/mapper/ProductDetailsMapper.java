package com.ekart.inventory.mapper;

import com.ekart.inventory.entity.Products;
import com.ekart.inventory.event.ProductDetailsResponseEvent;
import org.springframework.stereotype.Component;

@Component
public class ProductDetailsMapper {

    public  static ProductDetailsResponseEvent toProductDetailsEvent(Products products){

        ProductDetailsResponseEvent responseEvent = new ProductDetailsResponseEvent();
        responseEvent.setProductID(products.getProductID());
        responseEvent.setName(products.getName());
        responseEvent.setDescription(products.getDescription());
        responseEvent.setPrice(products.getPrice());
        responseEvent.setAvailableQty(products.getAvailableQty());

        return responseEvent;
    }
}

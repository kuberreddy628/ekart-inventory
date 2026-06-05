package com.ekart.inventory.service;

import com.ekart.inventory.dto.InventoryProductsRequestDTO;
import com.ekart.inventory.dto.InventoryProductsResponseDTO;
import com.ekart.inventory.dto.ProductDetailsResponseDTO;
import com.ekart.inventory.event.ProductDetailsResponseEvent;

import java.util.List;

public interface InventoryService {

    List<InventoryProductsResponseDTO> saveProducts(List<InventoryProductsRequestDTO> products);
    ProductDetailsResponseEvent getProductByID(Long id);
    List<ProductDetailsResponseDTO> getAllProducts();
}

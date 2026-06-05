package com.ekart.inventory.controller;

import com.ekart.inventory.dto.InventoryProductsRequestDTO;
import com.ekart.inventory.dto.ProductDetailsResponseDTO;
import com.ekart.inventory.event.ProductDetailsResponseEvent;
import com.ekart.inventory.service.InventoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/Inventory")
public class InventoryController {

    public static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    private InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/saveAll")
    public ResponseEntity<?> saveProducts(@Valid @RequestBody List<InventoryProductsRequestDTO> products, BindingResult result) {

        if (result.hasErrors()) {
            String errorMsg = result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(""));
            return ResponseEntity.badRequest().body(errorMsg);
        }
        logger.info("Received request to save all products and calling service method");
        return ResponseEntity.ok(inventoryService.saveProducts(products));
    }

    @GetMapping("/productByID/{productID}")
    public ResponseEntity<?> getProductByID(@PathVariable("productID") Long productID) {

        logger.info("Request received to get product by ID:{}", productID);
        try {
            ProductDetailsResponseEvent dto = inventoryService.getProductByID(productID);
            if (dto == null) {
                return ResponseEntity.badRequest().body(EntityNotFoundException.class);
            }
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<List<ProductDetailsResponseDTO>> getAllProducts() {
        logger.info("Received request to fetch all products");
        List<ProductDetailsResponseDTO> responseDTO = inventoryService.getAllProducts();
        return ResponseEntity.ok(responseDTO);
    }
}



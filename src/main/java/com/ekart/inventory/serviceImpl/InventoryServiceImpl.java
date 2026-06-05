package com.ekart.inventory.serviceImpl;

import com.ekart.inventory.dto.InventoryProductsRequestDTO;
import com.ekart.inventory.dto.InventoryProductsResponseDTO;
import com.ekart.inventory.dto.ProductDetailsResponseDTO;
import com.ekart.inventory.entity.Products;
import com.ekart.inventory.event.ProductDetailsResponseEvent;
import com.ekart.inventory.mapper.ProductDetailsMapper;
import com.ekart.inventory.repository.InventoryRepository;
import com.ekart.inventory.service.InventoryService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductDetailsMapper productDetailsMapper;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public List<InventoryProductsResponseDTO> saveProducts(List<InventoryProductsRequestDTO> products) {
        logger.info("Received request to save all the products");
        List<Products> list = new ArrayList<>();

        List<InventoryProductsResponseDTO> list1 = new ArrayList<>();

        for (InventoryProductsRequestDTO items : products) {
            Products pro = new Products();
            pro.setName(items.getName());
            pro.setDescription(items.getDescription());
            pro.setPrice(items.getPrice());
            pro.setAvailableQty(items.getAvailableQty());
            list.add(pro);
        }
        logger.info("Calling saveAll method to save all the products");
        List<Products> productsList = inventoryRepository.saveAll(list);
        logger.info("Products saved successfully");

        for (Products p : productsList) {
            InventoryProductsResponseDTO dto = new InventoryProductsResponseDTO();
            dto.setProductID(p.getProductID());
            dto.setName(p.getName());
            list1.add(dto);
        }
        return list1;
    }

    @Override
    public ProductDetailsResponseEvent getProductByID(Long id) {
        logger.info("Request received to fetch details by ID: {}, calling db", id);

        Products products = inventoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No Product found With ID:" + id));

        return ProductDetailsMapper.toProductDetailsEvent(products);
    }

    @Override
    public List<ProductDetailsResponseDTO> getAllProducts() {
        logger.info("Received request to fetch all the products, calling db");
        List<Products> products = inventoryRepository.findAll();
        logger.info("fetched all the products successfully from db");
        List<ProductDetailsResponseDTO> list = new ArrayList<>();
        for (Products dto : products) {
            ProductDetailsResponseDTO responseDTO = new ProductDetailsResponseDTO();
            responseDTO.setProductID(dto.getProductID());
            responseDTO.setName(dto.getName());
            responseDTO.setDescription(dto.getDescription());
            responseDTO.setPrice(dto.getPrice());
            responseDTO.setAvailableQty(dto.getAvailableQty());
            list.add(responseDTO);
        }
        return list;
    }
}

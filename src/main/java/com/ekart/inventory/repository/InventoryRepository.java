package com.ekart.inventory.repository;

import com.ekart.inventory.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Products, Long> {
}

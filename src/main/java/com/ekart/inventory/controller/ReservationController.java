package com.ekart.inventory.controller;


import com.ekart.inventory.dto.ReleaseResponseDTO;

import com.ekart.inventory.dto.ReleaseResponseDTO;
import com.ekart.inventory.dto.ReservationRequestDTO;
import com.ekart.inventory.dto.ReservationRequestDTO;

import com.ekart.inventory.dto.ReservationResponseDTO;
import com.ekart.inventory.dto.ReservationResponseDTO;

import com.ekart.inventory.exception.OrderAlreadyReleasedException;

import com.ekart.inventory.exception.OutOfStockException;

import com.ekart.inventory.service.ReservationService;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/reservations")
public class ReservationController {

    private static Logger logger = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }


    @PostMapping("/reserveProduct")
    public ResponseEntity<?> reserveProducts(@RequestBody ReservationRequestDTO requestDTO) throws OutOfStockException {
        try {
        logger.info("Received request to reserve the products and calling service");
        ReservationResponseDTO dto = reservationService.reserveProducts(requestDTO);

        if (dto == null) {
            return ResponseEntity.ok("Order failed, stock not available");
        }
        return ResponseEntity.ok(dto);
    }catch(IllegalStateException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    }

    @PostMapping("/release/{orderId}")
    public ResponseEntity<?> releaseProducts(@PathVariable Long orderId) throws OrderAlreadyReleasedException {
        try {
            logger.info("Received request to release or cancel the products with orderId: " + orderId);
            ReleaseResponseDTO responseDTO = reservationService.releaseProducts(orderId);
            return ResponseEntity.ok(responseDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch(OrderAlreadyReleasedException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }
    }

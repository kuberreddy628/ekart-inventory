package com.ekart.inventory.service;

import com.ekart.inventory.dto.ReleaseResponseDTO;
import com.ekart.inventory.dto.ReservationRequestDTO;
import com.ekart.inventory.dto.ReservationResponseDTO;
import com.ekart.inventory.exception.OrderAlreadyReleasedException;
import com.ekart.inventory.exception.OutOfStockException;

public interface ReservationService {

    ReservationResponseDTO reserveProducts (ReservationRequestDTO reservationRequestBTO)  throws OutOfStockException;
    ReleaseResponseDTO releaseProducts (Long orderId) throws OrderAlreadyReleasedException;
}


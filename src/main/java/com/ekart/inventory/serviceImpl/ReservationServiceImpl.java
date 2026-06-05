package com.ekart.inventory.serviceImpl;

import com.ekart.inventory.dto.*;
import com.ekart.inventory.entity.Products;
import com.ekart.inventory.entity.ReservationStatus;
import com.ekart.inventory.entity.Reservations;
import com.ekart.inventory.event.InventoryOrderOutcomeEvent;
import com.ekart.inventory.event.OrderItemKafkaPayload;
import com.ekart.inventory.event.OrderPlacedKafkaPayload;
import com.ekart.inventory.exception.OrderAlreadyReleasedException;
import com.ekart.inventory.exception.OutOfStockException;
import com.ekart.inventory.repository.InventoryRepository;
import com.ekart.inventory.repository.ReservationRepository;
import com.ekart.inventory.service.ReservationService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    private static final String TOPIC_ORDER_PLACED = "order-placed";
    private static final String TOPIC_INVENTORY_ORDER_SUCCESS = "inventory-order-success";
    private static final String TOPIC_INVENTORY_ORDER_FAILURE = "inventory-order-failure";
    private static final String CONSUMER_GROUP_ORDER_PLACED = "inventory-order-placed";

    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private final InventoryRepository inventoryRepository;

    private final ReservationRepository reservationRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ReservationServiceImpl(
            InventoryRepository inventoryRepository,
            ReservationRepository reservationRepository,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.inventoryRepository = inventoryRepository;
        this.reservationRepository = reservationRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = TOPIC_ORDER_PLACED, groupId = CONSUMER_GROUP_ORDER_PLACED)
    public void processOrderPlaced(OrderPlacedKafkaPayload event) {
        if (event == null || event.getOrderId() == null) {
            logger.warn("Ignored order-placed message with missing order id");
            return;
        }
        Long orderId = event.getOrderId();
        List<OrderItemKafkaPayload> items = event.getItems();
        if (items == null || items.isEmpty()) {
            kafkaTemplate.send(
                    TOPIC_INVENTORY_ORDER_FAILURE,
                    String.valueOf(orderId),
                    new InventoryOrderOutcomeEvent(orderId, "No line items"));
            return;
        }

        try {
            for (OrderItemKafkaPayload line : items) {
                if (line == null || line.getProductId() == null || line.getQuantity() == null) {
                    throw new IllegalStateException("Invalid line item: missing productId or quantity");
                }
                if (line.getQuantity() <= 0) {
                    throw new IllegalStateException("Invalid quantity for productId=" + line.getProductId());
                }
                ReservationRequestDTO req = new ReservationRequestDTO();
                req.setOrderId(orderId);
                req.setProductId(line.getProductId());
                req.setReservedQty(line.getQuantity());
                reserveProducts(req);
            }
            kafkaTemplate.send(
                    TOPIC_INVENTORY_ORDER_SUCCESS,
                    String.valueOf(orderId),
                    new InventoryOrderOutcomeEvent(orderId));
            logger.info("Reserved all lines for orderId={}, published {}", orderId, TOPIC_INVENTORY_ORDER_SUCCESS);
        } catch (OutOfStockException | IllegalStateException e) {
            rollbackQuietly(orderId);
            kafkaTemplate.send(
                    TOPIC_INVENTORY_ORDER_FAILURE,
                    String.valueOf(orderId),
                    new InventoryOrderOutcomeEvent(orderId, failureMessage(e)));
            logger.warn("Reservation failed for orderId={}: {}", orderId, e.getMessage());
        } catch (Exception e) {
            rollbackQuietly(orderId);
            kafkaTemplate.send(
                    TOPIC_INVENTORY_ORDER_FAILURE,
                    String.valueOf(orderId),
                    new InventoryOrderOutcomeEvent(orderId, failureMessage(e)));
            logger.error("Unexpected error reserving orderId=" + orderId, e);
        }
    }

    private static String failureMessage(Exception e) {
        String m = e.getMessage();
        return (m != null && !m.isBlank()) ? m : e.getClass().getSimpleName();
    }

    private void rollbackQuietly(Long orderId) {
        try {
            releaseProducts(orderId);
        } catch (Exception ex) {
            logger.warn("Rollback release failed for orderId {}: {}", orderId, ex.getMessage());
        }
    }

    @Override
    public ReservationResponseDTO reserveProducts(ReservationRequestDTO dto) throws OutOfStockException {
        Long orderId = dto.getOrderId();
        Long prodId = dto.getProductId();
        Long Qty = dto.getReservedQty();
        logger.info("Received to place order and reserving the products with orderId:" + orderId);
        // Fetch all existing reservations for this orderId (may be multiple products)
        java.util.List<Reservations> existingReservations = reservationRepository.findAllByOrderId(orderId);

        logger.info("Got reservations data and checking whether order already exists or not based on orderId and productId");

        // If any reservation for this order is already in RELEASED state, prevent reusing the same orderId
        boolean anyReleased = existingReservations.stream()
                .anyMatch(r -> ReservationStatus.RELEASED.equals(r.getStatus()));
        if (anyReleased) {
            throw new IllegalStateException("Order already Exists or released with orderId:" + orderId + " Please place an order with new orderId");
        }

        // If there is already a reservation for the same product in this order, block duplicates
        boolean sameProductReserved = existingReservations.stream()
                .anyMatch(r -> r.getProductId().equals(prodId) && ReservationStatus.RESERVED.equals(r.getStatus()));
        if (sameProductReserved) {
            throw new IllegalStateException("Product " + prodId + " is already reserved for orderId:" + orderId);
        }

        logger.info("If order doesn't exist in RELEASED state and product not yet reserved, continuing to place or reserve an order");
        Reservations reservations = new Reservations();
        ReservationResponseDTO responseDTO = new ReservationResponseDTO();

        Products managed = inventoryRepository.findById(prodId)
                .orElseThrow(() -> new EntityNotFoundException("No Product found with Id:" + prodId));

        Long availQty = managed.getAvailableQty();
        logger.info("checking whether requested product: {} and quantity {} available or not", prodId, Qty);

        if (availQty == null || availQty < Qty) {
            throw new OutOfStockException("OUT_OF_STOCK, Cannot proceed the Request");
        }

        long remainQty = availQty - Qty;
        managed.setAvailableQty(remainQty);
        inventoryRepository.save(managed);

        logger.info("updated product availableQty after reservation");

        responseDTO.setProductId(dto.getProductId());
        responseDTO.setOrderId(dto.getOrderId());
        responseDTO.setQuantity(dto.getReservedQty());
        ReservationStatus status = ReservationStatus.RESERVED;
        responseDTO.setStatus(status.name());
        responseDTO.setRemainingQty(remainQty);

        reservations.setOrderId(dto.getOrderId());
        reservations.setReservedQty(dto.getReservedQty());
        reservations.setProductId(dto.getProductId());
        reservations.setStatus(ReservationStatus.RESERVED);
        reservations.setCreatedAt(LocalDateTime.now());
        reservations.setUpdatedAt(LocalDateTime.now());
        reservationRepository.save(reservations);

        logger.info("Product reserved with requested quantity");
        logger.info("sending response back to user");
        return responseDTO;
    }

    @Override
    public ReleaseResponseDTO releaseProducts(Long orderId) throws OrderAlreadyReleasedException {
        logger.info("received request to cancel or release the order with orderId: " + orderId);
        logger.info("checking whether the order Exists or not in reservations with orderId: "+orderId);

        java.util.List<Reservations> reservationsForOrder = reservationRepository.findAllByOrderId(orderId);
        if (reservationsForOrder.isEmpty()) {
            throw new EntityNotFoundException("No reservations present with orderId:" + orderId);
        }

        logger.info("If order exists, getting product details from Products to update the quantity for all reserved items");
        ReleaseResponseDTO responseDTO = new ReleaseResponseDTO();
        List<ReleasedProductsDTO> list = new ArrayList<>();

        logger.info("checking if requested orderId equals orderId in reservations and status is in RESERVED state");

        boolean anyStillReserved = false;

        for (Reservations reserved : reservationsForOrder) {
            if (!ReservationStatus.RESERVED.equals(reserved.getStatus())) {
                continue;
            }

            anyStillReserved = true;

            Products existingProducts = inventoryRepository.findById(reserved.getProductId()).orElseThrow();
            Long availableQty = existingProducts.getAvailableQty();
            Long totalQty = availableQty + reserved.getReservedQty();

            reserved.setStatus(ReservationStatus.RELEASED);
            reserved.setUpdatedAt(LocalDateTime.now());
            logger.info("updating reservations table if orderId equals requested orderId and status is RESERVED for productId: {}", reserved.getProductId());
            reservationRepository.save(reserved);

            existingProducts.setAvailableQty(totalQty);
            inventoryRepository.save(existingProducts);
            logger.info("updated Products table with released items Quantity for productId: {}", existingProducts.getProductID());

            ReleasedProductsDTO dto = new ReleasedProductsDTO();
            dto.setProductId(reserved.getProductId());
            dto.setReleasedQty(reserved.getReservedQty());
            dto.setAvailableQty(existingProducts.getAvailableQty());
            list.add(dto);
        }

        if (!anyStillReserved) {
            throw new OrderAlreadyReleasedException("order with Id: " + orderId + " has been already released");
        }

        responseDTO.setOrderId(orderId);
        responseDTO.setStatus(ReservationStatus.RELEASED.name());
        responseDTO.setReleasedItems(list);

        logger.info("sending Response back to user");
        return responseDTO;

    }
}


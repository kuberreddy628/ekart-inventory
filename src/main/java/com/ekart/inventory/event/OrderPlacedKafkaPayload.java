package com.ekart.inventory.event;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Mirrors {@code com.ekart.orders.event.OrderPlacedEvent} JSON from topic {@code order-placed}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderPlacedKafkaPayload {

    /** Orders publishes {@code orderId}; {@code OrderID}/{@code orderID} kept for older messages. */
    @JsonProperty("orderId")
    @JsonAlias({"OrderID", "orderID"})
    private Long orderId;
    private Long customerID;
    private List<OrderItemKafkaPayload> items;
    private AddressKafkaPayload address;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Long customerID) {
        this.customerID = customerID;
    }

    public List<OrderItemKafkaPayload> getItems() {
        return items;
    }

    public void setItems(List<OrderItemKafkaPayload> items) {
        this.items = items;
    }

    public AddressKafkaPayload getAddress() {
        return address;
    }

    public void setAddress(AddressKafkaPayload address) {
        this.address = address;
    }
}

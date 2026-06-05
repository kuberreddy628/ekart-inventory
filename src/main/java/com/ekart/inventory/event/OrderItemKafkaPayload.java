package com.ekart.inventory.event;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Mirrors {@code com.ekart.orders.event.OrderItemEvent} JSON from {@code order-placed}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItemKafkaPayload {

    @JsonProperty("productId")
    @JsonAlias({"ProductID", "productID"})
    private Long productId;
    private Long quantity;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}

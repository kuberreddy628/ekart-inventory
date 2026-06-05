package com.ekart.inventory.event;

/**
 * Published to {@code inventory-order-success} or {@code inventory-order-failure}.
 * Uses {@code failureReason} so JSON matches {@code com.ekart.orders.event.fulfillment.InventoryOrderOutcomeEvent}.
 */
public class InventoryOrderOutcomeEvent {

	private Long orderId;
	private String failureReason;

	public InventoryOrderOutcomeEvent() {
	}

	public InventoryOrderOutcomeEvent(Long orderId) {
		this.orderId = orderId;
	}

	public InventoryOrderOutcomeEvent(Long orderId, String failureReason) {
		this.orderId = orderId;
		this.failureReason = failureReason;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}
}

package com.warehouse.dataTransfereObjects;

import java.math.BigDecimal;

//Response for the total-inventory-value endpoint.
// totalValue the sum of (price x quantity) across every product in the warehouse.

public record InventoryValueResponse(BigDecimal totalValue) {
}

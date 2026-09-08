package com.warehouse.dataTransfereObjects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

//for a full update (PUT) of an existing product. The id comes from the URL path not the body, so it is not included here unitsSold optional popularity count; treated as 0 when omitted.

public record UpdateProductRequest(
        @NotBlank(message = "name must not be blank")
        String name,

        @NotBlank(message = "category must not be blank")
        String category,

        @NotNull(message = "price is required")
        @PositiveOrZero(message = "price must be zero or positive")
        BigDecimal price,

        @PositiveOrZero(message = "quantity must be zero or positive")
        int quantity,

        @NotNull(message = "expiryDate is required")
        LocalDate expiryDate,

        @PositiveOrZero(message = "unitsSold must be zero or positive")
        Long unitsSold
) {
}

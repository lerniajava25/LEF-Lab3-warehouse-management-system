package com.warehouse.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Product(String id,String name, String category, BigDecimal price, int quantity, LocalDate expiryDate,                      long unitsSold) {
    public Product {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Product category must not be blank");
        }
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("Product price must be zero or positive");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Product quantity must be zero or positive");
        }
        if (unitsSold < 0) {
            throw new IllegalArgumentException("Units sold must be zero or positive");
        }
    }

    //return the value of this product's stock (price multiplied by quantity)
    public BigDecimal stockValue() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}


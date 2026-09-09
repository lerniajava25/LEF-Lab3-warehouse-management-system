package com.warehouse.exception;

//Thrown when an operation references a product id that does not exist. Mapped to HTTP 404 (Not Found) by GlobalExceptionHandler

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String id) {
        super("Product not found: " + id);
    }
}

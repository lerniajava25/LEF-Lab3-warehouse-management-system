package com.warehouse.controller;

import com.warehouse.domain.Product;
import com.warehouse.dataTransfereObjects.CreateProductRequest;
import com.warehouse.dataTransfereObjects.InventoryValueResponse;
import com.warehouse.dataTransfereObjects.UpdateProductRequest;
import com.warehouse.service.WarehouseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;

//REST API for the warehouse. Each method maps a URL + HTTP verb to a service call and returns the proper HTTP
  //status code (201 Created, 200 OK, 204 No Content, ...).
  //It contains no business logic of its own. that all lives in  WarehouseService

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final WarehouseService service;

    public ProductController(com.warehouse.service.WarehouseService service) {
        this.service = service;
    }

    // ----------------------------- CRUD -----------------------------

    //POST /api/products -> 201 Created, with a Location header pointing at the new resource.
    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody CreateProductRequest request,
                                          UriComponentsBuilder uriBuilder) {
        Product created = service.createProduct(request);
        URI location = uriBuilder.path("/api/products/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    // GET /api/products -> 200 OK with the full list.
    @GetMapping
    public List<Product> getAll() {
        return service.getAllProducts();
    }

    // GET /api/products/{id} -> 200 OK, or 404 if it does not exist.
    @GetMapping("/{id}")
    public Product getById(@PathVariable String id) {
        return service.getProduct(id);
    }

    //PUT /api/products/{id} -> 200 OK, or 404 if it does not exist.
    @PutMapping("/{id}")
    public Product update(@PathVariable String id,
                          @Valid @RequestBody UpdateProductRequest request) {
        return service.updateProduct(id, request);
    }

    //DELETE /api/products/{id} -> 204 No Content, or 404 if it does not exist
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ----------------------- Search & Filter -----------------------

    //GET /api/products/category/{category} -> products in that category.
    @GetMapping("/category/{category}")
    public List<Product> getByCategory(@PathVariable String category) {
        return service.findByCategory(category);
    }

    //GET /api/products/low-stock?threshold=10 -> products below the given stock level.
    @GetMapping("/low-stock")
    public List<Product> getLowStock(@RequestParam int threshold) {
        return service.findLowStock(threshold);
    }

    // --------------------- Analysis & Aggregation ---------------------

    //GET /api/products/total-inventory-value -> total value of all products in stock.
    @GetMapping("/total-inventory-value")
    public BigDecimal totalInventoryValue() {
        return service.totalInventoryValue();
    }

    //GET /api/products/average-price-per-category -> average product price for each category.
    @GetMapping("/average-price-per-category")
    public Map<String, BigDecimal> averagePricePerCategory() {
        return service.averagePricePerCategory();
    }



    // ------------------------------ Sorting ------------------------------

    @GetMapping("/top-by-price")
    public List<Product> topByPrice(@RequestParam int limit) {
        return service.topNByPrice(limit);
    }

    @GetMapping("/top-by-popularity")
    public List<Product> topByPopularity(@RequestParam int limit) {
        return service.topNByPopularity(limit);
    }



}

package com.warehouse.service;

import com.warehouse.domain.Product;
import com.warehouse.dataTransfereObjects.CreateProductRequest;
import com.warehouse.dataTransfereObjects.UpdateProductRequest;
import com.warehouse.exception.ProductNotFoundException;
import com.warehouse.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


//Holds all business logic for the warehouse,  CRUD operations delegate storage to the ProductRepository, All search, filtering, aggregation and sorting operations are implemented with the Java Streams API over the in memory collection returned by the repository.
@Service
public class WarehouseService {

    private final ProductRepository repository;

    public WarehouseService(ProductRepository repository) {
        this.repository = repository;
    }

    // ---------------------------------------------------------------------
    // CRUD
    // ---------------------------------------------------------------------

    //Creates a new product with a server-generated id.
    public Product createProduct(CreateProductRequest request) {
        String id = UUID.randomUUID().toString();
        Product product = new Product(
                id,
                request.name(),
                request.category(),
                request.price(),
                request.quantity(),
                request.expiryDate(),
                request.unitsSold() == null ? 0L : request.unitsSold()
        );
        return repository.save(product);
    }

    // Returns a single product or throws ProductNotFoundException
    public Product getProduct(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    //Returns every product currently in the warehouse.
    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    //Fully replaces an existing product, keeping its id.
    public Product updateProduct(String id, UpdateProductRequest request) {
        if (!repository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        Product updated = new Product(
                id,
                request.name(),
                request.category(),
                request.price(),
                request.quantity(),
                request.expiryDate(),
                request.unitsSold() == null ? 0L : request.unitsSold()
        );
        return repository.save(updated);
    }

    //Deletes a product, or throws if it does not exist.
    public void deleteProduct(String id) {
        boolean removed = repository.deleteById(id);
        if (!removed) {
            throw new ProductNotFoundException(id);
        }
    }

    // ---------------------------------------------------------------------
    // Requirement 1: Search & Filter
    // ---------------------------------------------------------------------

    //Returns all products in the given category (case-insensitive).
    public List<Product> findByCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category must not be blank");
        }
        return getAllProducts().stream()
                .filter(product -> product.category().equalsIgnoreCase(category))
                .toList();
    }

    //Returns products whose stock is strictly below threshold, used to warn about low balance. Results are sorted from lowest stock upward so the most urgent items appear first.

    public List<Product> findLowStock(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold must be zero or positive");
        }
        return getAllProducts().stream()
                .filter(product -> product.quantity() < threshold)
                .sorted(Comparator.comparingInt(Product::quantity))
                .toList();
    }

    // ---------------------------------------------------------------------
    // Requirement 2: Analysis & Aggregation
    // ---------------------------------------------------------------------
    public BigDecimal totalInventoryValue() {
        return getAllProducts().stream()
                .map(Product::stockValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<String, BigDecimal> averagePricePerCategory() {
        return getAllProducts().stream()
                .collect(Collectors.groupingBy(
                        Product::category,
                        Collectors.collectingAndThen(
                                Collectors.mapping(Product::price, Collectors.toList()),
                                prices -> {
                                    BigDecimal sum = prices.stream()
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                                    return sum.divide(
                                            BigDecimal.valueOf(prices.size()),
                                            2,
                                            RoundingMode.HALF_UP
                                    );

                                }
                        )
                ));

    }


    // ---------------------------------------------------------------------
    // Requirement 3: Sorting
    // ---------------------------------------------------------------------

    public List<Product> topNByPrice(int n) {
        return getAllProducts().stream()
                .sorted(Comparator.comparing(Product::price).reversed())
                .limit(n)
                .toList();
    }

    public List<Product> topNByPopularity(int n) {
        return getAllProducts().stream()
                .sorted(Comparator.comparing(Product::unitsSold).reversed())
                .limit(n)
                .toList();
    }
}

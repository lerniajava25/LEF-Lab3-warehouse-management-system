package com.warehouse.repository;

import com.warehouse.domain.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

//In memory implementation of  ProductRepository:
 // Products are held in a  ConcurrentHashMap rather than a plain java.util.ArrayList. Because the application is a web server, many request threads may read and write at the same time. A regular  ArrayList is not thread safe and would risk corrupted state or ConcurrentModificationExceptions.ConcurrentHashMap allows safe concurrent access and gives O(1) lookup by id

@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final Map<String, Product> products = new ConcurrentHashMap<>();

    @Override
    public Product save(Product product) {
        products.put(product.id(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAll() {
        // Return an immutable copy so callers cannot modify the backing store directly.
        return List.copyOf(products.values());
    }

    @Override
    public boolean deleteById(String id) {
        return products.remove(id) != null;
    }

    @Override
    public boolean existsById(String id) {
        return products.containsKey(id);
    }
}

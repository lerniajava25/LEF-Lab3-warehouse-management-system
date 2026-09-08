
package com.warehouse.repository;

import com.warehouse.domain.Product;

import java.util.List;
import java.util.Optional;

    //Storage abstraction for Product instances.Declaring the storage as an interface keeps the service layer independent of how products are stored.
    //It also makes the service easy to unit test: the repository can be replaced by a Mockito mock so the business logic can be verified in isolation.

    public interface ProductRepository {

        //Inserts a new product or replaces an existing one with the same id.
        Product save(Product product);

        // Finds a product by id or an empty Optional if none exists.
        Optional<Product> findById(String id);

        //Returns a snapshot of all stored products.
        List<Product> findAll();

        //Deletes the product with the given id. return true if a product was removed.
        boolean deleteById(String id);

        //return true if a product with the given id exists.
        boolean existsById(String id);
    }



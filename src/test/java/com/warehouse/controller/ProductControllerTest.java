package com.warehouse.controller;

import com.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import com.warehouse.domain.Product;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import org.springframework.http.MediaType;


@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WarehouseService service;

    // ----------------------------- CRUD -----------------------------
    // Tests GET /api/products - returns all products
    @Test
    void getAllReturnsProducts() throws Exception {

        Product product = new Product(
                "1",
                "Laptop",
                "Electronics",
                new BigDecimal("1000"),
                5,
                LocalDate.of(2026, 9, 10),
                0L
        );

        Mockito.when(service.getAllProducts())
                .thenReturn(List.of(product));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    // Tests GET /api/products/{id} - returns a product by id
    @Test
    void getByIdReturnsProduct() throws Exception {

        Product product = new Product(
                "1",
                "Laptop",
                "Electronics",
                new BigDecimal("1000"),
                5,
                LocalDate.of(2026, 9, 10),
                0L
        );

        Mockito.when(service.getProduct("1"))
                .thenReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    // Tests POST /api/products - creates a product and returns 201 Created
    @Test
    void createReturnsCreatedProduct() throws Exception {

        Product createdProduct = new Product(
                "1",
                "Laptop",
                "Electronics",
                new BigDecimal("1000"),
                5,
                LocalDate.of(2026, 9, 10),
                0L
        );

        Mockito.when(service.createProduct(Mockito.any()))
                .thenReturn(createdProduct);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Laptop",
                                  "category": "Electronics",
                                  "price": 1000,
                                  "quantity": 5,
                                  "expiryDate": "2026-09-10",
                                  "unitsSold": 0
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/products/1"))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    // Tests PUT /api/products/{id} - updates an existing product
    @Test
    void updateReturnsUpdatedProduct() throws Exception {

        Product updatedProduct = new Product(
                "1",
                "Laptop",
                "Electronics",
                new BigDecimal("1200"),
                8,
                LocalDate.of(2026, 9, 10),
                0L
        );

        Mockito.when(service.updateProduct(Mockito.eq("1"), Mockito.any()))
                .thenReturn(updatedProduct);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Laptop",
                                  "category": "Electronics",
                                  "price": 1200,
                                  "quantity": 8,
                                  "expiryDate": "2026-09-10",
                                  "unitsSold": 0
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(1200))
                .andExpect(jsonPath("$.quantity").value(8));
    }

    // Tests DELETE /api/products/{id} - deletes a product and returns 204 No Content
        @Test
        void deleteReturnsNoContent() throws Exception {

            mockMvc.perform(delete("/api/products/1"))
                    .andExpect(status().isNoContent());

            Mockito.verify(service).deleteProduct("1");
        }

    // Tests GET /api/products/category/{category} - returns products in the selected category
    @Test
    void getByCategoryReturnsProducts() throws Exception {

        Product product = new Product(
                "1",
                "Laptop",
                "Electronics",
                new BigDecimal("1000"),
                5,
                LocalDate.of(2026, 9, 10),
                0L
        );

        Mockito.when(service.findByCategory("Electronics"))
                .thenReturn(List.of(product));

        mockMvc.perform(get("/api/products/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Electronics"));
    }

    // Tests GET /api/products/low-stock - returns products below the stock threshold
    @Test
    void getLowStockReturnsProducts() throws Exception {

        Product product = new Product(
                "1",
                "Laptop",
                "Electronics",
                new BigDecimal("1000"),
                5,
                LocalDate.of(2026, 9, 10),
                0L
        );

        Mockito.when(service.findLowStock(10))
                .thenReturn(List.of(product));

        mockMvc.perform(get("/api/products/low-stock")
                        .param("threshold", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantity").value(5));
    }

    // --------------------- Analysis & Aggregation ---------------------

    // Tests GET /api/products/total-inventory-value
    @Test
    void totalInventoryValueReturnsValue() throws Exception {
        Mockito.when(service.totalInventoryValue())
                .thenReturn(new BigDecimal("2150"));

        mockMvc.perform(get("/api/products/total-inventory-value"))
                .andExpect(status().isOk())
                .andExpect(content().string("2150"));
    }

    // Tests GET /api/products/average-price-per-category
    @Test
    void averagePricePerCategoryReturnsValues() throws Exception {
        Mockito.when(service.averagePricePerCategory())
                .thenReturn(Map.of(
                        "Electronics", new BigDecimal("525.00"),
                        "Furniture", new BigDecimal("500.00")
                ));

        mockMvc.perform(get("/api/products/average-price-per-category"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                      "Electronics": 525.00,
                      "Furniture": 500.00
                    }
                    """));
    }

    // ------------------------------ Sorting ------------------------------

    // Tests GET /api/products/top-by-price - returns products sorted by price

    @Test
    void topByPriceReturnsProducts() throws Exception {

        Product product1 = new Product(
                "1",
                "Laptop",
                "Electronics",
                new BigDecimal("1500"),
                5,
                LocalDate.of(2026, 9, 10),
                0L
        );

        Product product2 = new Product(
                "2",
                "Mouse",
                "Electronics",
                new BigDecimal("50"),
                10,
                LocalDate.of(2026, 9, 10),
                0L
        );

        Mockito.when(service.topNByPrice(2))
                .thenReturn(List.of(product1, product2));

        mockMvc.perform(get("/api/products/top-by-price")
                        .param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").value(1500))
                .andExpect(jsonPath("$[1].price").value(50));
    }

    // Tests GET /api/products/top-by-popularity - returns products sorted by popularity
    @Test
    void topByPopularityReturnsProducts() throws Exception {

        Product product1 = new Product(
                "1",
                "Laptop",
                "Electronics",
                new BigDecimal("1500"),
                5,
                LocalDate.of(2026, 9, 10),
                100L
        );

        Product product2 = new Product(
                "2",
                "Mouse",
                "Electronics",
                new BigDecimal("50"),
                10,
                LocalDate.of(2026, 9, 10),
                50L
        );

        Mockito.when(service.topNByPopularity(2))
                .thenReturn(List.of(product1, product2));

        mockMvc.perform(get("/api/products/top-by-popularity")
                        .param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].unitsSold").value(100))
                .andExpect(jsonPath("$[1].unitsSold").value(50));
    }
    }

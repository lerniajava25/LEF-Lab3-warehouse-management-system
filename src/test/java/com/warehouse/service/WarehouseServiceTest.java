package com.warehouse.service;

import org.junit.jupiter.api.Test;
import com.warehouse.repository.ProductRepository;
import org.mockito.Mockito;

import com.warehouse.domain.Product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WarehouseServiceTest {


    // --------------------- Analysis & Aggregation ---------------------

    // Tests calculation of the total inventory value
@Test
void totalInventoryValueCalculatesCorrectTotal() {
    ProductRepository repository = Mockito.mock(ProductRepository.class);
    WarehouseService service = new WarehouseService(repository);

    Product product1 = new Product(
            "1",
            "Laptop",
            "Electronics",
            new BigDecimal("1000"),
            2,
            LocalDate.of(2026, 9, 9),
            0L
    );

    Product product2 = new Product(
            "2",
            "Mouse",
            "Electronics",
            new BigDecimal("50"),
            3,
            LocalDate.of(2026, 9, 9),
            0L
    );

    Mockito.when(repository.findAll())
            .thenReturn(List.of(product1, product2));

    BigDecimal result = service.totalInventoryValue();

    assertEquals(new BigDecimal("2150"), result);
}

    // Tests calculation of the average price for each category
    @Test
    void shouldCalculateAveragePricePerCategory() {
        ProductRepository repository = Mockito.mock(ProductRepository.class);
        WarehouseService service = new WarehouseService(repository);

        Product product1 = new Product(
                "1",
                "Laptop",
                "Electronics",
                new BigDecimal("1000"),
                2,
                LocalDate.of(2026, 9, 9),
                0L
        );
        Product product2 = new Product(
                "2",
                "Mouse",
                "Electronics",
                new BigDecimal("50"),
                5,
                LocalDate.of(2026, 9, 9),
                0L
        );

        Product product3 = new Product(
                "3",
                "Desk",
                "Furniture",
                new BigDecimal("500"),
                1,
                LocalDate.of(2026, 9, 9),
                0L
        );

        Mockito.when(repository.findAll())
                .thenReturn(List.of(product1, product2, product3));
        Map<String, BigDecimal> result = service.averagePricePerCategory();
        assertEquals(
                new BigDecimal("525.00"),
                result.get("Electronics")
        );
        assertEquals(
                new BigDecimal("500.00"),
                result.get("Furniture")
        );
    }

    // ------------------------------ Sorting ------------------------------

    // Tests that topNByPrice returns the most expensive product first
    @Test
    void topNByPriceReturnsMostExpensiveProducts() {
        ProductRepository repository = Mockito.mock(ProductRepository.class);
        WarehouseService service = new WarehouseService(repository);

        Product product1 = new Product(
                "1",
                "Laptop",
                "Electronics",
                new BigDecimal("1000"),
                2,
                LocalDate.of(2026, 9, 9),
                10L
        );
        Product product2 = new Product(
                "2",
                "Phone",
                "Electronics",
                new BigDecimal("1500"),
                1,
                LocalDate.of(2026, 9, 9),
                5L
        );

        Mockito.when(repository.findAll())
                .thenReturn(List.of(product1, product2));

        List<Product> result = service.topNByPrice(1);
        assertEquals("2", result.get(0).id());
    }

    // Tests that topNByPopularity returns the product with the most units sold first
    @Test
    void topNByPopularityReturnsMostPopularProducts() {
        ProductRepository repository = Mockito.mock(ProductRepository.class);
        WarehouseService service = new WarehouseService(repository);

        Product product1 = new Product(
                "1",
                "Laptop",
                "Electronics",
                new BigDecimal("1000"),
                2,
                LocalDate.of(2026, 9, 9),
                10L
        );
        Product product2 = new Product(
                "2",
                "Phone",
                "Electronics",
                new BigDecimal("1500"),
                1,
                LocalDate.of(2026, 9, 9),
                20L
        );

        Mockito.when(repository.findAll())
                .thenReturn(List.of(product1, product2));

        List<Product> result = service.topNByPopularity(1);
        assertEquals("2", result.get(0).id());
    }
}

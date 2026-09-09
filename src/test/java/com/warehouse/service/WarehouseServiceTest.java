package com.warehouse.service;

import org.junit.jupiter.api.Test;
import com.warehouse.repository.ProductRepository;
import org.mockito.Mockito;

import com.warehouse.domain.Product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WarehouseServiceTest {


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

    service.totalInventoryValue();
    assertEquals(new BigDecimal("2150"), result);


}
}

package com.warehouse.controller;

import com.warehouse.service.WarehouseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@AutoConfigureMockMvc

class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WarehouseService service;


    @Test
    void totalInventoryValueReturnsValue() throws Exception {
        Mockito.when(service.totalInventoryValue())
                .thenReturn(new BigDecimal("2150"));

        mockMvc.perform(get("/api/products/total-inventory-value"))
                .andExpect(status().isOk())
                .andExpect(content().string("2150"));
    }

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
}

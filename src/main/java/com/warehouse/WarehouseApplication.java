package com.warehouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//Entry point for the Warehouse Management System (main)Starts an embedded web server and wires together the layered architecture:Controller -> Service -> Repository (in-memory)

@SpringBootApplication
public class WarehouseApplication {

       public static void main(String[] args) {
        SpringApplication.run(WarehouseApplication.class, args);

    }
}

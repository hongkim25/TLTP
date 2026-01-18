package com.hong.thebaker.entity; // PACKAGED AS ENTITY

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal; // Money

@Entity
@Getter @Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal price; // money -> BigDecimal even for KRW
    private int stockQuantity;

    // NEW FIELD
    private String category; // "HARD", "SOFT", "ALL"

    public Product() {}

    public Product(String name, BigDecimal price, int stockQuantity, String category) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }
}
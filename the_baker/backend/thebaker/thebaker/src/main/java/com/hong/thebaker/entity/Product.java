package com.hong.thebaker.entity; // PACKAGED AS ENTITY

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter @Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal price; // money -> BigDecimal even for KRW
    private int stockQuantity;
    private String category; // "HARD", "SOFT", "ALL"

    public Product() {}

    public Product(String name, BigDecimal price, int stockQuantity, String category) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }

    @Lob // Large Object
    @Column(columnDefinition = "TEXT") // Allows larger base64 strings
    private String imageBase64;

    // Getter and Setter
    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}
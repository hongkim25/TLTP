package com.hong.thebaker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Getter @Setter @NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal price; // Money should always be BigDecimal

    private String category; // "BREAD", "CAKE", "DRINK"

    @Column(length = 1000) // Allow longer text for AI descriptions
    private String description;

    private String imageUrl;

    private boolean isAvailable;

    private int stockQuantity;
}
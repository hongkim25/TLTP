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

    private BigDecimal price; // 돈은 언제나 BigDecimal

    private String category; // 빵, 음료 등

    @Column(length = 1000) // 혹시나 길어질 수 있어서 1000으로 설정
    private String description;

    private String imageUrl;

    private boolean isAvailable;

    private int stockQuantity;
}
package com.hong.thebaker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to Customer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private int pointsUsed;
    private int pointsEarned;
    private String memo;

    @Column(name = "pickup_time")
    private String pickupTime; // Stores "12:00 PM", "1:00 PM" etc.

    @Column(name = "is_takeaway")
    private boolean isTakeaway; // true = To Go

    @Column(name = "wants_cut")
    private boolean wantsCut;   // true = Cut

    @Column(name = "is_archived")
    private boolean isArchived = false;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // Link to the items in this order
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.orderDate = LocalDateTime.now();
        if (this.status == null) {
            this.status = OrderStatus.PENDING;
        }
    }
}
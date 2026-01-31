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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private LocalDateTime orderDate;
    private LocalDateTime cancelledDate;
    private BigDecimal totalAmount;
    private int pointsUsed;
    private int pointsEarned;
    private String memo;

    @Column(name = "pickup_time")
    private String pickupTime; // Stores "12:00 PM", "1:00 PM" etc.

    @Column(name = "is_takeaway")
    private Boolean isTakeaway; // true = To Go

    @Column(name = "wants_cut")
    private Boolean wantsCut;   // true = Cut

    @Column(name = "is_archived")
    private Boolean isArchived = false;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // Link to the items in this order
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.orderDate = LocalDateTime.now();
        if (this.status == null) {
            this.status = OrderStatus.PENDING;
        }
    }
}
package com.hong.thebaker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(unique = true) // No two people can have the same phone
    private String phone;
    private int points;

    @Column(unique = true)
    private String qrCode;

    private LocalDateTime createdAt;

    private boolean marketingConsent; // New Field

    public boolean isMarketingConsent() { return marketingConsent; }
    public void setMarketingConsent(boolean marketingConsent) { this.marketingConsent = marketingConsent; }

    // Relationship: "One Customer has Many Orders"
    // This connects the Customer table to the Order table
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();

    // This runs automatically before saving to the database
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.points == 0) {
            this.points = 0; // Default to 0 if not set
        }
    }
}
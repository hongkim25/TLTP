package com.hong.thebaker.repository;

import com.hong.thebaker.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 1. Keeping for future use or specific customer lookups
    List<Order> findByCustomerId(Long customerId);

    // 2. Staff Dashboard Query (Hides Archived, Sorts by Newest)
    List<Order> findByIsArchivedFalseOrderByOrderDateDesc();

    // 3. My Page Search "Find orders by the phone number stored in the Customer entity"
    List<Order> findByCustomerPhoneNumberOrderByOrderDateDesc(String phoneNumber);
}
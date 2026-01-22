package com.hong.thebaker.repository;

import com.hong.thebaker.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 1. For finding orders by Customer ID
    List<Order> findByCustomerId(Long customerId);

    // 2. For Staff Dashboard (Hides Archived orders)
    List<Order> findByIsArchivedFalseOrderByOrderDateDesc();

    // 3. "Find orders where Customer -> Phone matches the input, sort by date"
    List<Order> findByCustomerPhoneOrderByOrderDateDesc(String phone);
}
package com.hong.thebaker.repository;

import com.hong.thebaker.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // "SELECT * FROM orders WHERE customer_id = ?"
    List<Order> findByCustomerId(Long customerId);
}
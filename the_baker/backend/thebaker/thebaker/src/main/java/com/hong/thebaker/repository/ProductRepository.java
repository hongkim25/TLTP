package com.hong.thebaker.repository;

import com.hong.thebaker.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Custom query: "Find all products that are currently available"
    // Spring Data JPA writes the SQL for you automatically based on the method name!
    List<Product> findByIsAvailableTrue();

    // Custom query: "Find by category" (e.g., give me all Cakes)
    List<Product> findByCategory(String category);
}
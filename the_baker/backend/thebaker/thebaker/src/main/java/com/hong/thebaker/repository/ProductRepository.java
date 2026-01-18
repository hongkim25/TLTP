package com.hong.thebaker.repository;

import com.hong.thebaker.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Custom query: "Find by category" (e.g., give me all Cakes)
    List<Product> findByCategory(String category);
}
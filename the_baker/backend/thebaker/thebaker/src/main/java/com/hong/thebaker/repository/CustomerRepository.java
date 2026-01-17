package com.hong.thebaker.repository;

import com.hong.thebaker.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Needed for Login: Find a customer by their phone number
    Optional<Customer> findByPhone(String phone);

    // Needed for QR Code scan
    Optional<Customer> findByQrCode(String qrCode);
}
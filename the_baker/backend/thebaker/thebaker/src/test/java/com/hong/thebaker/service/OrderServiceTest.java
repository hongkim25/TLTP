package com.hong.thebaker.service;

import com.hong.thebaker.entity.Order;
import com.hong.thebaker.entity.OrderItem; // <--- Import this
import com.hong.thebaker.entity.OrderStatus;
import com.hong.thebaker.entity.Product;
import com.hong.thebaker.repository.OrderRepository;
import com.hong.thebaker.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private ProductRepository productRepo;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("Inventory Restoration: Canceling an order should increase stock")
    void cancelOrder_ShouldRestockProduct() {
        // 1. GIVEN
        // Product: Bagel (Stock = 10)
        Product bagel = new Product("Bagel", BigDecimal.valueOf(3.5), 10, "bread");

        // Order: Status Confirmed
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.COMPLETED);

        // OrderItem: 2 Bagels
        OrderItem item = new OrderItem();
        item.setProduct(bagel);
        item.setQuantity(2);
        item.setOrder(order); // Link back to order

        // Add Item to Order's List
        order.getItems().add(item);

        // Mock Database Response
        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        // 2. WHEN (Cancel the Order)
        orderService.cancelOrder(1L);

        // 3. THEN (Verify Stock)
        // Original Stock (10) + Canceled Qty (2) = 12
        assertEquals(12, bagel.getStockQuantity());

        // Verify changes were saved
        verify(productRepo).save(bagel);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }
}
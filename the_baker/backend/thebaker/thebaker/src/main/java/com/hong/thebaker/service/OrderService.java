package com.hong.thebaker.service;

import com.hong.thebaker.dto.OrderRequest;
import com.hong.thebaker.entity.*;
import com.hong.thebaker.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public OrderService(ProductRepository productRepository,
                        OrderRepository orderRepository,
                        CustomerRepository customerRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
    }

    public Order createOrder(OrderRequest request) {
        // 1. Find OR Create Customer
        Customer customer = customerRepository.findByPhone(request.getPhoneNumber())
                .orElseGet(() -> {
                    // Logic: If not found, create them instantly!
                    Customer newCustomer = new Customer();
                    newCustomer.setName("Guest " + request.getPhoneNumber());
                    newCustomer.setPhone(request.getPhoneNumber());
                    newCustomer.setPoints(0);
                    return customerRepository.save(newCustomer);
                });

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(java.time.LocalDateTime.now());
        order.setStatus(OrderStatus.COMPLETED);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // 2. Loop through requested items
        for (OrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

            // 3. Inventory Check (Business Logic)
            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("재고가 충분하지 않습니다: " + product.getName());
            }

            // 4. Reduce Stock
            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            productRepository.save(product);

            // 5. Create OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());

            orderItems.add(orderItem);

            // Calculate running total
            BigDecimal lineItemTotal = product.getPrice().multiply(new BigDecimal(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(lineItemTotal);
        }

        // 6. Finalize Order
        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        // 7. Calculate Points (Logic: 5% of total amount)
        int pointsEarned = totalAmount.multiply(BigDecimal.valueOf(0.05)).intValue();
        order.setPointsEarned(pointsEarned);

        // 8. Update Customer's Total Points
        customer.setPoints(customer.getPoints() + pointsEarned);
        customerRepository.save(customer);

        return orderRepository.save(order);
    }
}

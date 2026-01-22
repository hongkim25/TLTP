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
                    Customer newCustomer = new Customer();
                    newCustomer.setPhone(request.getPhoneNumber());
                    newCustomer.setPoints(0);
                    return newCustomer;
                });

        // --- FIX 1: UPDATE NAME CORRECTLY ---
        // If a name is provided in the request, ALWAYS update the customer's name. This fixes the "GUEST" issue.
        if (request.getCustomerName() != null && !request.getCustomerName().isEmpty()) {
            customer.setName(request.getCustomerName());
        } else if (customer.getName() == null) {
            customer.setName("Guest");
        }

        // ALWAYS update the consent based on the latest order
        customer.setMarketingConsent(request.isMarketingConsent());
        customerRepository.save(customer);

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul")).toLocalDateTime());
        order.setStatus(OrderStatus.COMPLETED);
        order.setMemo(request.getMemo());
        order.setPickupTime(request.getPickupTime());
        order.setTakeaway(request.isTakeaway());
        order.setWantsCut(request.isWantsCut());

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


        // 7-A. Handle Point Usage (Redemption)
        int pointsToUse = request.getPointsToUse();
        if (pointsToUse > 0) {
            if (customer.getPoints() < pointsToUse) {
                throw new RuntimeException("포인트가 부족합니다. 보유 포인트: " + customer.getPoints());
            }
            // Subtract used points
            customer.setPoints(customer.getPoints() - pointsToUse);
        }

        // B. Calculate "Net Pay" (Total - PointsUsed)
        // We only give points on the actual money paid
        BigDecimal pointsUsedBd = BigDecimal.valueOf(pointsToUse);
        BigDecimal netPayAmount = totalAmount.subtract(pointsUsedBd);

        // Safety check: Cannot use more points than total price
        if (netPayAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("결제 금액보다 많은 포인트를 사용할 수 없습니다.");
        }

        // C. Handle Point Earning based on Payment Method
        PaymentMethod method = request.getPaymentMethod();

        if (method == null) {
            method = PaymentMethod.CARD; // 기본값 카드
        }

        int pointsEarned = method.calculatePoints(netPayAmount);
        order.setPointsEarned(pointsEarned);
        customer.setPoints(customer.getPoints() + pointsEarned);

        customerRepository.save(customer);
        return orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Already cancelled");
        }

        // 1. Restore Stock
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        // 2. Change Status
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }


    public List<Order> getAllOrders() {
        return orderRepository.findByIsArchivedFalseOrderByOrderDateDesc();
    }

    // Logic for Sidebar Quick Payment
    public void processQuickPayment(com.hong.thebaker.dto.QuickPaymentRequest request) {

        // 1. Find or Create Customer
        Customer customer = customerRepository.findByPhone(request.getPhoneNumber())
                .orElseGet(() -> {
                    Customer newCustomer = new Customer();
                    newCustomer.setPhone(request.getPhoneNumber());
                    newCustomer.setName("Guest");
                    newCustomer.setPoints(0);
                    return customerRepository.save(newCustomer);
                });

        // 2. Use Points (Redemption)
        if (request.getPointsToUse() > 0) {
            if (customer.getPoints() < request.getPointsToUse()) {
                throw new RuntimeException("포인트가 부족합니다. 보유 포인트: " + customer.getPoints());
            }
            customer.setPoints(customer.getPoints() - request.getPointsToUse());
        }

        // 3. Calculate "Net Pay" (Total - Points Used)
        BigDecimal totalAmountBd = BigDecimal.valueOf(request.getTotalAmount());
        BigDecimal pointsUsedBd = BigDecimal.valueOf(request.getPointsToUse());
        BigDecimal netPayAmount = totalAmountBd.subtract(pointsUsedBd);

        if (netPayAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("결제 금액보다 많은 포인트를 사용할 수 없습니다.");
        }

        // 4. Earn Points (Cash 3%, Card 1%)
        PaymentMethod method = request.getPaymentMethod();
        if (method == null) method = PaymentMethod.CARD; // Default safety

        int pointsEarned = method.calculatePoints(netPayAmount);

        customer.setPoints(customer.getPoints() + pointsEarned);
        customerRepository.save(customer);
    }

    // Archive Order
    public void archiveOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setArchived(true);
        orderRepository.save(order);
    }

    // Find My Orders
    public List<Order> findMyOrders(String phone) {
        return orderRepository.findByCustomerPhoneOrderByOrderDateDesc(phone);
    }
}

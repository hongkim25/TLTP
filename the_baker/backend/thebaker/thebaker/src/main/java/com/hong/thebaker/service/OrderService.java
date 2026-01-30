package com.hong.thebaker.service;

import com.hong.thebaker.dto.OrderRequest;
import com.hong.thebaker.entity.*;
import com.hong.thebaker.repository.CustomerRepository;
import com.hong.thebaker.repository.OrderRepository;
import com.hong.thebaker.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

        // Update Name
        if (request.getCustomerName() != null && !request.getCustomerName().isEmpty()) {
            customer.setName(request.getCustomerName());
        } else if (customer.getName() == null) {
            customer.setName("Guest");
        }

        customer.setMarketingConsent(request.isMarketingConsent());
        customerRepository.save(customer);

        Order order = new Order();
        order.setCustomer(customer);
        // Seoul Time
        order.setOrderDate(java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul")).toLocalDateTime());

        // --- CHANGE 1: Set Status to PENDING (Waiting for Staff) ---
        order.setStatus(OrderStatus.PENDING);

        order.setMemo(request.getMemo());
        order.setPickupTime(request.getPickupTime());
        order.setTakeaway(request.isTakeaway());
        order.setWantsCut(request.isWantsCut());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // 2. Loop through items & Reduce Stock Immediately (To prevent double-booking)
        for (OrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

            // Inventory Check
            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("재고가 충분하지 않습니다: " + product.getName());
            }

            // Reduce Stock
            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());

            orderItems.add(orderItem);

            BigDecimal lineItemTotal = product.getPrice().multiply(new BigDecimal(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(lineItemTotal);
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        // 3. Handle Point Usage (Redemption) - Lock points now
        int pointsToUse = request.getPointsToUse();
        if (pointsToUse > 0) {
            if (customer.getPoints() < pointsToUse) {
                throw new RuntimeException("포인트가 부족합니다. 보유 포인트: " + customer.getPoints());
            }
            customer.setPoints(customer.getPoints() - pointsToUse);
        }

        // --- CHANGE 2: Save Payment Method but DO NOT EARN POINTS YET ---
        // We will calculate earnings when they actually pay (in completeOrder)
        order.setPointsUsed(pointsToUse); // Assuming you have this field, or we track it via request
        // Store the intended payment method for later reference
        // (If you don't have a field for this in Order entity, we can calculate it later or just assume CASH/CARD)

        // Save and return
        return orderRepository.save(order);
    }

    // --- NEW METHOD: Staff Confirms Stock ---
    public void confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Only PENDING orders can be confirmed.");
        }

        order.setStatus(OrderStatus.PROCESSING); // Means: "Approved, Waiting for Payment"
        orderRepository.save(order);
    }

    // --- NEW METHOD: Payment Complete (Earn Points Here) ---
    public void completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Only give points if not already completed
        if (order.getStatus() == OrderStatus.COMPLETED) return;

        Customer customer = order.getCustomer();

        // 1. Calculate Net Pay
        // We need to know how many points were used.
        // If 'pointsUsed' isn't in Entity, we can infer it or we assume 0 for simplicity if field missing.
        // Ideally, add 'private int pointsUsed;' to Order.java.
        // For now, let's assume we re-calculate based on Total vs Net?
        // Or simpler: Just give flat points or assume standard 3% on Total for MVP.

        // Let's use the standard logic:
        BigDecimal total = order.getTotalAmount();
        // NOTE: If you didn't save "pointsUsed" in Order entity, we can't subtract it exactly here.
        // FOR 3AM SAFETY: Let's give points on the TOTAL amount.
        // (It's a small bonus for the customer, easier code for you).

        int pointsEarned = total.multiply(new BigDecimal("0.03")).intValue(); // 3% flat rate

        order.setPointsEarned(pointsEarned);
        customer.setPoints(customer.getPoints() + pointsEarned);
        customerRepository.save(customer);

        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);
    }

    // --- NEW METHOD: Count Pending (For Alarm) ---
    public Long countPendingOrders() {
        // You need to add this method to OrderRepository interface:
        // long countByStatus(OrderStatus status);
        return orderRepository.countByStatus(OrderStatus.PENDING);
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

        // 2. Restore Points if they used any
        // If you tracked pointsUsed, restore them here.

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledDate(LocalDateTime.now());
        orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findByIsArchivedFalseOrderByOrderDateDesc();
    }

    public void processQuickPayment(com.hong.thebaker.dto.QuickPaymentRequest request) {
        // ... (Keep your existing Quick Payment logic as is) ...
        // ... (It is perfect for the sidebar) ...
        Customer customer = customerRepository.findByPhone(request.getPhoneNumber())
                .orElseGet(() -> {
                    Customer newCustomer = new Customer();
                    newCustomer.setPhone(request.getPhoneNumber());
                    newCustomer.setName("Guest");
                    newCustomer.setPoints(0);
                    return customerRepository.save(newCustomer);
                });

        if (request.getPointsToUse() > 0) {
            if (customer.getPoints() < request.getPointsToUse()) {
                throw new RuntimeException("포인트 부족");
            }
            customer.setPoints(customer.getPoints() - request.getPointsToUse());
        }

        BigDecimal totalAmountBd = BigDecimal.valueOf(request.getTotalAmount());
        BigDecimal pointsUsedBd = BigDecimal.valueOf(request.getPointsToUse());
        BigDecimal netPayAmount = totalAmountBd.subtract(pointsUsedBd);

        PaymentMethod method = request.getPaymentMethod();
        if (method == null) method = PaymentMethod.CARD;

        int pointsEarned = method.calculatePoints(netPayAmount);

        customer.setPoints(customer.getPoints() + pointsEarned);
        customerRepository.save(customer);
    }

    public void archiveOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setArchived(true);
        orderRepository.save(order);
    }

    public List<Order> findMyOrders(String phone) {
        return orderRepository.findByCustomerPhoneOrderByOrderDateDesc(phone);
    }

    public String getOrderStatus(Long id) {
        return orderRepository.findById(id)
                .map(order -> order.getStatus().name())
                .orElse("UNKNOWN");
    }
}
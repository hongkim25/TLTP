package com.hong.thebaker.dto;

import com.hong.thebaker.entity.PaymentMethod;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private Long customerId;
    private String customerName;
    private String phoneNumber;
    private String memo;
    private List<OrderItemRequest> items;
    private boolean marketingConsent;
    private int pointsToUse;
    private PaymentMethod paymentMethod;
    private boolean isTakeaway;
    private boolean wantsCut;

    @Data
    public static class OrderItemRequest {
        private Long productId;
        private int quantity;
    }
}

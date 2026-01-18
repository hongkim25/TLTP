package com.hong.thebaker.dto;

import com.hong.thebaker.entity.PaymentMethod;
import lombok.Data;

@Data
public class QuickPaymentRequest {
    private String phoneNumber;
    private double totalAmount;   // <--- This is the key field the Sidebar sends!
    private int pointsToUse;
    private PaymentMethod paymentMethod;
}
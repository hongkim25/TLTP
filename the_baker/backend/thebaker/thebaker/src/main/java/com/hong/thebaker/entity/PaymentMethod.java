package com.hong.thebaker.entity;

import java.math.BigDecimal;

public enum PaymentMethod {
    CASH(0.03), // 현금 3%
    CARD(0.03); // 카드 3%

    private final double rate;

    PaymentMethod(double rate) {
        this.rate = rate;
    }

    // Points calculation logic (Service가 할 일을 얘가 대신 해줌 -> 객체지향적)
    public int calculatePoints(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(this.rate)).intValue();
    }
}

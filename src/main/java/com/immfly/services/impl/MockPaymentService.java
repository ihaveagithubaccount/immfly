package com.immfly.services.impl;

import com.immfly.services.PaymentService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class MockPaymentService implements PaymentService {
    private final ConcurrentHashMap<String, PaymentStatus> paymentStatuses = new ConcurrentHashMap<>();

    @Override
    public void processPayment(BigDecimal amount, String cardToken) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid payment amount");
        }

        if (cardToken == null || cardToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid card token");
        }

        if (cardToken.startsWith("9999")) {
            throw new RuntimeException("Payment failed: Card declined");
        }

        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Payment processing interrupted");
        }

        paymentStatuses.put(cardToken, PaymentStatus.SUCCESS);
    }

    private enum PaymentStatus {
        SUCCESS,
        FAILED
    }
} 
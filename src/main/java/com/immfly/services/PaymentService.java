package com.immfly.services;

import java.math.BigDecimal;

public interface PaymentService {
    void processPayment(BigDecimal amount, String cardToken) throws Exception;
} 
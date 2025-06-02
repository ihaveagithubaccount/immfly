package com.immfly.services;

import com.immfly.services.impl.MockPaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class MockPaymentServiceTest {
    private MockPaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new MockPaymentService();
    }

    @Test
    @DisplayName("Should successfully process valid payment")
    void testSuccessfulPayment() throws Exception {
        BigDecimal amount = new BigDecimal("100.00");
        String cardToken = "1234567890123456";

        assertDoesNotThrow(() -> paymentService.processPayment(amount, cardToken));
    }

    @Test
    @DisplayName("Should fail payment with invalid amount")
    void testPaymentWithInvalidAmount() {
        BigDecimal invalidAmount = BigDecimal.ZERO;
        String cardToken = "1234567890123456";

        assertThrows(IllegalArgumentException.class, 
            () -> paymentService.processPayment(invalidAmount, cardToken));
    }

    @Test
    @DisplayName("Should fail payment with invalid card token")
    void testPaymentWithInvalidCardToken() {
        BigDecimal amount = new BigDecimal("100.00");
        String invalidCardToken = "";

        assertThrows(IllegalArgumentException.class, 
            () -> paymentService.processPayment(amount, invalidCardToken));
    }

    @ParameterizedTest
    @ValueSource(strings = {"9999000000000000", "9999000000000001"})
    @DisplayName("Should fail payment with known problematic cards")
    void testPaymentWithProblematicCards(String cardToken) {
        BigDecimal amount = new BigDecimal("100.00");

        assertThrows(Exception.class, 
            () -> paymentService.processPayment(amount, cardToken));
    }

    @Test
    @DisplayName("Should handle payment gateway timeout")
    void testPaymentGatewayTimeout() {
        BigDecimal amount = new BigDecimal("100.00");
        String cardToken = "9999000000000000";

        assertThrows(RuntimeException.class, 
            () -> paymentService.processPayment(amount, cardToken));
    }

    @Test
    @DisplayName("Should handle concurrent payment attempts")
    void testConcurrentPaymentAttempts() throws Exception {
        BigDecimal amount = new BigDecimal("100.00");
        String cardToken = "1234567890123456";

        Thread t1 = new Thread(() -> {
            try {
                paymentService.processPayment(amount, cardToken);
            } catch (Exception e) {
                fail("First payment attempt failed", e);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                paymentService.processPayment(amount, cardToken);
            } catch (Exception e) {
                fail("Second payment attempt failed", e);
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
} 
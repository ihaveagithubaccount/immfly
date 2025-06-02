package com.immfly.models;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testOrderCreation() {
        Order order = Order.builder()
                .id(1L)
                .buyerEmail("test@example.com")
                .seatLetter("A")
                .seatNumber(1)
                .status(OrderStatus.OPEN)
                .paymentStatus(PaymentStatus.PAYMENT_FAILED)
                .totalPrice(new BigDecimal("99.99"))
                .items(new ArrayList<>())
                .build();

        assertNotNull(order);
        assertEquals(1L, order.getId());
        assertEquals("test@example.com", order.getBuyerEmail());
        assertEquals("A", order.getSeatLetter());
        assertEquals(1, order.getSeatNumber());
        assertEquals(OrderStatus.OPEN, order.getStatus());
        assertEquals(PaymentStatus.PAYMENT_FAILED, order.getPaymentStatus());
        assertEquals(new BigDecimal("99.99"), order.getTotalPrice());
        assertNotNull(order.getItems());
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    void testOrderWithItems() {
        Product product1 = Product.builder()
                .id(1L)
                .name("Product 1")
                .price(new BigDecimal("10.00"))
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .name("Product 2")
                .price(new BigDecimal("20.00"))
                .build();

        OrderItem item1 = OrderItem.builder()
                .product(product1)
                .quantity(2)
                .build();

        OrderItem item2 = OrderItem.builder()
                .product(product2)
                .quantity(1)
                .build();

        Order order = Order.builder()
                .id(1L)
                .buyerEmail("test@example.com")
                .seatLetter("A")
                .seatNumber(1)
                .items(new ArrayList<>())
                .build();

        order.getItems().add(item1);
        order.getItems().add(item2);
        order.setTotalPrice(
            item1.getProduct().getPrice().multiply(BigDecimal.valueOf(item1.getQuantity()))
            .add(item2.getProduct().getPrice().multiply(BigDecimal.valueOf(item2.getQuantity())))
        );

        assertEquals(2, order.getItems().size());
        assertEquals(new BigDecimal("40.00"), order.getTotalPrice());
    }

    @Test
    void testOrderStatusTransitions() {
        Order order = Order.builder()
                .id(1L)
                .buyerEmail("test@example.com")
                .seatLetter("A")
                .seatNumber(1)
                .status(OrderStatus.OPEN)
                .paymentStatus(PaymentStatus.PAYMENT_FAILED)
                .build();

        order.setStatus(OrderStatus.FINISHED);
        order.setPaymentStatus(PaymentStatus.PAID);

        assertEquals(OrderStatus.FINISHED, order.getStatus());
        assertEquals(PaymentStatus.PAID, order.getPaymentStatus());
    }

    @Test
    void testOrderEquality() {
        Order order1 = Order.builder()
                .id(1L)
                .buyerEmail("test@example.com")
                .seatLetter("A")
                .seatNumber(1)
                .status(OrderStatus.OPEN)
                .paymentStatus(PaymentStatus.PAYMENT_FAILED)
                .build();

        Order order2 = Order.builder()
                .id(1L)
                .buyerEmail("test@example.com")
                .seatLetter("A")
                .seatNumber(1)
                .status(OrderStatus.OPEN)
                .paymentStatus(PaymentStatus.PAYMENT_FAILED)
                .build();

        Order order3 = Order.builder()
                .id(2L)
                .buyerEmail("different@example.com")
                .seatLetter("B")
                .seatNumber(2)
                .status(OrderStatus.FINISHED)
                .paymentStatus(PaymentStatus.PAID)
                .build();

        assertEquals(order1, order2);
        assertEquals(order1.hashCode(), order2.hashCode());
        assertNotEquals(order1, order3);
        assertNotEquals(order1.hashCode(), order3.hashCode());
    }

    @Test
    void testOrderToString() {
        Order order = Order.builder()
                .id(1L)
                .buyerEmail("test@example.com")
                .seatLetter("A")
                .seatNumber(1)
                .status(OrderStatus.OPEN)
                .paymentStatus(PaymentStatus.PAYMENT_FAILED)
                .totalPrice(new BigDecimal("99.99"))
                .build();

        String toString = order.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("buyerEmail=test@example.com"));
        assertTrue(toString.contains("seatLetter=A"));
        assertTrue(toString.contains("seatNumber=1"));
        assertTrue(toString.contains("status=OPEN"));
        assertTrue(toString.contains("paymentStatus=PAYMENT_FAILED"));
        assertTrue(toString.contains("totalPrice=99.99"));
    }
} 
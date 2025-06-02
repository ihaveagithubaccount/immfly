package com.immfly.models;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void testOrderItemCreation() {
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .build();

        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .build();

        assertNotNull(orderItem);
        assertEquals(1L, orderItem.getId());
        assertEquals(product, orderItem.getProduct());
        assertEquals(2, orderItem.getQuantity());
        assertEquals(new BigDecimal("199.98"), product.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
    }

    @Test
    void testOrderItemEquality() {
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .build();

        OrderItem item1 = OrderItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .build();

        OrderItem item2 = OrderItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .build();

        OrderItem item3 = OrderItem.builder()
                .id(2L)
                .product(product)
                .quantity(1)
                .build();

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
        assertNotEquals(item1, item3);
        assertNotEquals(item1.hashCode(), item3.hashCode());
    }

    @Test
    void testOrderItemToString() {
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .build();

        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .build();

        String toString = orderItem.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("quantity=2"));
    }

    @Test
    void testOrderItemWithProduct() {
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .build();

        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .product(product)
                .quantity(2)
                .build();

        assertNotNull(orderItem.getProduct());
        assertEquals(product, orderItem.getProduct());
    }

    @Test
    void testOrderItemWithOrder() {
        Order order = Order.builder()
                .id(1L)
                .buyerEmail("test@example.com")
                .seatLetter("A")
                .seatNumber(1)
                .build();

        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .order(order)
                .quantity(2)
                .build();

        assertNotNull(orderItem.getOrder());
        assertEquals(order, orderItem.getOrder());
    }
} 
package com.immfly.models;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testProductCreation() {
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .build();

        assertNotNull(product);
        assertEquals(1L, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals(new BigDecimal("99.99"), product.getPrice());
    }

    @Test
    void testProductEquality() {
        Product product1 = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .build();

        Product product2 = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .build();

        Product product3 = Product.builder()
                .id(2L)
                .name("Different Product")
                .price(new BigDecimal("149.99"))
                .build();

        assertEquals(product1, product2);
        assertEquals(product1.hashCode(), product2.hashCode());
        assertNotEquals(product1, product3);
        assertNotEquals(product1.hashCode(), product3.hashCode());
    }

    @Test
    void testProductToString() {
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .build();

        String toString = product.toString();
        assertTrue(toString.contains("Test Product"));
        assertTrue(toString.contains("99.99"));
    }

    @Test
    void testProductWithCategory() {
        Category category = Category.builder()
                .id(1L)
                .name("Test Category")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .category(category)
                .build();

        assertNotNull(product.getCategory());
        assertEquals(category, product.getCategory());
    }
} 
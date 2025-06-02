package com.immfly.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void testCategoryCreation() {
        Category parentCategory = Category.builder()
                .id(1L)
                .name("Parent Category")
                .build();

        Category childCategory = Category.builder()
                .id(2L)
                .name("Child Category")
                .parent(parentCategory)
                .build();

        assertNotNull(parentCategory);
        assertEquals(1L, parentCategory.getId());
        assertEquals("Parent Category", parentCategory.getName());
        assertNull(parentCategory.getParent());

        assertNotNull(childCategory);
        assertEquals(2L, childCategory.getId());
        assertEquals("Child Category", childCategory.getName());
        assertEquals(parentCategory, childCategory.getParent());
    }

    @Test
    void testCategoryEquality() {
        Category category1 = Category.builder()
                .id(1L)
                .name("Test Category")
                .build();

        Category category2 = Category.builder()
                .id(1L)
                .name("Test Category")
                .build();

        Category category3 = Category.builder()
                .id(2L)
                .name("Different Category")
                .build();

        assertEquals(category1, category2);
        assertEquals(category1.hashCode(), category2.hashCode());
        assertNotEquals(category1, category3);
        assertNotEquals(category1.hashCode(), category3.hashCode());
    }

    @Test
    void testCategoryToString() {
        Category category = Category.builder()
                .id(1L)
                .name("Test Category")
                .build();

        String toString = category.toString();
        assertTrue(toString.contains("Test Category"));
    }
} 
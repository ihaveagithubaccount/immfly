package com.immfly.services;

import com.immfly.models.Category;
import com.immfly.repositories.CategoryRepository;
import com.immfly.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryRepository);
    }

    @Test
    void getAllCategories_ShouldReturnAllCategories() {
        Category category1 = Category.builder()
                .id(1L)
                .name("Category 1")
                .build();

        Category category2 = Category.builder()
                .id(2L)
                .name("Category 2")
                .build();

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));

        List<Category> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        assertEquals("Category 1", result.get(0).getName());
        assertEquals("Category 2", result.get(1).getName());
    }

    @Test
    void getCategoryById_WithValidId_ShouldReturnCategory() {
        Category category = Category.builder()
                .id(1L)
                .name("Test Category")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Category", result.getName());
    }

    @Test
    void getCategoryById_WithInvalidId_ShouldThrowException() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            categoryService.getCategoryById(999L)
        );
    }

    @Test
    void createCategory_ShouldSaveAndReturnCategory() {
        Category category = Category.builder()
                .name("New Category")
                .build();

        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

        Category result = categoryService.createCategory(category);

        assertNotNull(result);
        assertEquals("New Category", result.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_WithParent_ShouldSaveAndReturnCategory() {
        Category parentCategory = Category.builder()
                .id(1L)
                .name("Parent Category")
                .build();

        Category category = Category.builder()
                .name("Child Category")
                .parent(parentCategory)
                .build();

        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

        Category result = categoryService.createCategory(category);

        assertNotNull(result);
        assertEquals("Child Category", result.getName());
        assertEquals(parentCategory, result.getParent());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_WithValidId_ShouldUpdateAndReturnCategory() {
        Category existingCategory = Category.builder()
                .id(1L)
                .name("Old Name")
                .build();

        Category updatedCategory = Category.builder()
                .name("New Name")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

        Category result = categoryService.updateCategory(1L, updatedCategory);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Name", result.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_WithParent_ShouldUpdateAndReturnCategory() {
        Category existingCategory = Category.builder()
                .id(1L)
                .name("Old Name")
                .build();

        Category updatedCategory = Category.builder()
                .name("New Name")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

        Category result = categoryService.updateCategory(1L, updatedCategory);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Name", result.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_WithInvalidId_ShouldThrowException() {
        Category category = Category.builder()
                .name("Test Category")
                .build();

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            categoryService.updateCategory(999L, category)
        );
    }

    @Test
    void deleteCategory_WithValidId_ShouldDeleteCategory() {
        Category category = Category.builder()
                .id(1L)
                .name("Test Category")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).delete(any(Category.class));

        categoryService.deleteCategory(1L);

        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_WithInvalidId_ShouldThrowException() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            categoryService.deleteCategory(999L)
        );
    }
} 
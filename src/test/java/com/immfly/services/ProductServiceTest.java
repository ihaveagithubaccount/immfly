package com.immfly.services;

import com.immfly.models.Product;
import com.immfly.models.Category;
import com.immfly.repositories.ProductRepository;
import com.immfly.repositories.CategoryRepository;
import com.immfly.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, categoryRepository);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
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

        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<Product> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("Product 1", result.get(0).getName());
        assertEquals("Product 2", result.get(1).getName());
    }

    @Test
    void getProductById_WithValidId_ShouldReturnProduct() {
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(new BigDecimal("10.00"))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Product", result.getName());
    }

    @Test
    void getProductById_WithInvalidId_ShouldThrowException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            productService.getProductById(999L)
        );
    }

    @Test
    void createProduct_ShouldSaveAndReturnProduct() {
        Product product = Product.builder()
                .name("New Product")
                .price(new BigDecimal("15.00"))
                .build();

        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));
        when(productRepository.findByNameIgnoreCase(any())).thenReturn(List.of());

        Product result = productService.createProduct(product);

        assertNotNull(result);
        assertEquals("New Product", result.getName());
        assertEquals(new BigDecimal("15.00"), result.getPrice());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_WithCategory_ShouldSaveAndReturnProduct() {
        Category category = Category.builder()
                .id(1L)
                .name("Test Category")
                .build();

        Product product = Product.builder()
                .name("New Product")
                .price(new BigDecimal("15.00"))
                .category(category)
                .build();

        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));
        when(productRepository.findByNameIgnoreCase(any())).thenReturn(List.of());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Product result = productService.createProduct(product);

        assertNotNull(result);
        assertEquals("New Product", result.getName());
        assertEquals(new BigDecimal("15.00"), result.getPrice());
        assertEquals(category, result.getCategory());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_WithValidId_ShouldUpdateAndReturnProduct() {
        Product existingProduct = Product.builder()
                .id(1L)
                .name("Old Name")
                .price(new BigDecimal("10.00"))
                .build();

        Product updatedProduct = Product.builder()
                .name("New Name")
                .price(new BigDecimal("20.00"))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));
        when(productRepository.findByNameIgnoreCase(any())).thenReturn(List.of());

        Product result = productService.updateProduct(1L, updatedProduct);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Name", result.getName());
        assertEquals(new BigDecimal("20.00"), result.getPrice());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_WithCategory_ShouldUpdateAndReturnProduct() {
        Category category = Category.builder()
                .id(1L)
                .name("Test Category")
                .build();

        Product existingProduct = Product.builder()
                .id(1L)
                .name("Old Name")
                .price(new BigDecimal("10.00"))
                .build();

        Product updatedProduct = Product.builder()
                .name("New Name")
                .price(new BigDecimal("20.00"))
                .category(category)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));
        when(productRepository.findByNameIgnoreCase(any())).thenReturn(List.of());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Product result = productService.updateProduct(1L, updatedProduct);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Name", result.getName());
        assertEquals(new BigDecimal("20.00"), result.getPrice());
        assertEquals(category, result.getCategory());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_WithInvalidId_ShouldThrowException() {
        Product product = Product.builder()
                .name("Test Product")
                .build();

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            productService.updateProduct(999L, product)
        );
    }

    @Test
    void deleteProduct_WithValidId_ShouldDeleteProduct() {
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(any(Product.class));

        productService.deleteProduct(1L);

        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_WithInvalidId_ShouldThrowException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            productService.deleteProduct(999L)
        );
    }

    @Test
    void getProductsByCategory_ShouldReturnProductsInCategory() {
        Category category = Category.builder()
                .id(1L)
                .name("Test Category")
                .build();

        Product product1 = Product.builder()
                .id(1L)
                .name("Product 1")
                .price(new BigDecimal("10.00"))
                .category(category)
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .name("Product 2")
                .price(new BigDecimal("20.00"))
                .category(category)
                .build();

        when(productRepository.findByCategoryId(1L)).thenReturn(Arrays.asList(product1, product2));

        List<Product> result = productService.getProductsByCategory(1L);

        assertEquals(2, result.size());
        assertEquals("Product 1", result.get(0).getName());
        assertEquals("Product 2", result.get(1).getName());
        assertEquals(category, result.get(0).getCategory());
        assertEquals(category, result.get(1).getCategory());
    }
} 
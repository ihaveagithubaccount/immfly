package com.immfly.services;

import com.immfly.models.Product;
import com.immfly.models.Category;
import com.immfly.repositories.ProductRepository;
import com.immfly.repositories.CategoryRepository;
import com.immfly.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Transactional
    public Product createProduct(Product product) {
        validateProduct(product);
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + product.getCategory().getId()));
            product.setCategory(category);
        }
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);
        validateProduct(productDetails);
        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());
        product.setImageUrl(productDetails.getImageUrl());
        
        if (productDetails.getCategory() != null && productDetails.getCategory().getId() != null) {
            Category category = categoryRepository.findById(productDetails.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDetails.getCategory().getId()));
            product.setCategory(category);
        }
        
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    private void validateProduct(Product product) {
        if (product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        
        boolean isDuplicate = productRepository.findByNameIgnoreCase(product.getName())
                .stream()
                .anyMatch(p -> !p.getId().equals(product.getId()));
        
        if (isDuplicate) {
            throw new IllegalArgumentException("Product with name '" + product.getName() + "' already exists");
        }
    }
} 
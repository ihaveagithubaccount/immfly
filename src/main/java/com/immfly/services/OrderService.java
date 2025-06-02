package com.immfly.services;

import com.immfly.models.*;
import com.immfly.repositories.OrderRepository;
import com.immfly.repositories.ProductRepository;
import com.immfly.exceptions.ResourceNotFoundException;
import com.immfly.exceptions.PaymentProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PaymentService paymentService;

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    @Transactional
    public Order createOrder(Order order) {
        order.setStatus(OrderStatus.OPEN);
        order.setPaymentStatus(PaymentStatus.PAYMENT_FAILED);
        validateOrderItems(order);
        calculateTotalPrice(order);
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrder(Long id, Order orderDetails) {
        Order existingOrder = getOrderById(id);
        
        existingOrder.setBuyerEmail(orderDetails.getBuyerEmail());
        existingOrder.setSeatLetter(orderDetails.getSeatLetter());
        existingOrder.setSeatNumber(orderDetails.getSeatNumber());
        
        existingOrder.getItems().clear();
        
        if (orderDetails.getItems() != null) {
            Set<Long> productIds = new HashSet<>();
            
            for (OrderItem item : orderDetails.getItems()) {
                Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + item.getProduct().getId()));
                
                if (!productIds.add(product.getId())) {
                    throw new IllegalArgumentException("Duplicate product in order: " + product.getName());
                }
                
                OrderItem newItem = OrderItem.builder()
                    .order(existingOrder)
                    .product(product)
                    .quantity(item.getQuantity())
                    .build();
                
                existingOrder.getItems().add(newItem);
            }
        }
        
        validateOrderItems(existingOrder);
        calculateTotalPrice(existingOrder);
        
        return orderRepository.save(existingOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Transactional
    public Order processPayment(Long id, String cardToken) {
        Order order = getOrderById(id);
        
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new PaymentProcessingException("Order is already paid");
        }
        
        try {
            paymentService.processPayment(order.getTotalPrice(), cardToken);
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setStatus(OrderStatus.FINISHED);
            order.setPaymentDate(LocalDateTime.now());
            order.setPaymentGateway("ONLINE_PAYMENT");
            order.setCardToken(cardToken);
            return orderRepository.save(order);
        } catch (Exception e) {
            order.setPaymentStatus(PaymentStatus.PAYMENT_FAILED);
            orderRepository.save(order);
            throw new PaymentProcessingException("Payment processing failed: " + e.getMessage());
        }
    }

    @Transactional
    public Order processOfflinePayment(Long id) {
        Order order = getOrderById(id);
        
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new PaymentProcessingException("Order is already paid");
        }
        
        order.setPaymentStatus(PaymentStatus.OFFLINE_PAYMENT);
        order.setStatus(OrderStatus.FINISHED);
        order.setPaymentDate(LocalDateTime.now());
        order.setPaymentGateway("OFFLINE_PAYMENT");
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    private void validateOrderItems(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }

        for (OrderItem item : order.getItems()) {
            if (item.getProduct() == null || item.getProduct().getId() == null) {
                throw new IllegalArgumentException("Order item must have a valid product");
            }
            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Order item quantity must be greater than 0");
            }
        }
    }

    private void calculateTotalPrice(Order order) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            BigDecimal itemTotal = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);
        }
        order.setTotalPrice(total);
    }
} 
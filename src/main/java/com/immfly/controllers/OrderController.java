package com.immfly.controllers;

import com.immfly.models.*;
import com.immfly.services.OrderService;
import com.immfly.dto.OrderRequest;
import com.immfly.repositories.ProductRepository;
import com.immfly.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final ProductRepository productRepository;

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping
    public Order createOrder(@RequestBody OrderRequest request) {
        Order order = Order.builder()
                .buyerEmail(request.getBuyerEmail())
                .seatLetter(request.getSeatLetter())
                .seatNumber(request.getSeatNumber())
                .items(new ArrayList<>())
                .build();

        request.getItems().forEach(itemRequest -> {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.getProductId()));
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .build();
            order.getItems().add(orderItem);
        });

        return orderService.createOrder(order);
    }

    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Long id, @RequestBody OrderRequest request) {
        Order order = Order.builder()
                .buyerEmail(request.getBuyerEmail())
                .seatLetter(request.getSeatLetter())
                .seatNumber(request.getSeatNumber())
                .items(request.getItems().stream()
                        .map(itemRequest -> {
                            Product product = productRepository.findById(itemRequest.getProductId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.getProductId()));
                            return OrderItem.builder()
                                    .product(product)
                                    .quantity(itemRequest.getQuantity())
                                    .build();
                        })
                        .collect(Collectors.toList()))
                .build();
        return orderService.updateOrder(id, order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/payment")
    public Order processPayment(@PathVariable Long id, @RequestParam String cardToken) {
        return orderService.processPayment(id, cardToken);
    }

    @PostMapping("/{id}/offline-payment")
    public Order processOfflinePayment(@PathVariable Long id) {
        return orderService.processOfflinePayment(id);
    }

    @PutMapping("/{id}/status")
    public Order updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return orderService.updateOrderStatus(id, status);
    }
} 
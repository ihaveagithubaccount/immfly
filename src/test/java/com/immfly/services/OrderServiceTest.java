package com.immfly.services;

import com.immfly.models.*;
import com.immfly.repositories.OrderRepository;
import com.immfly.repositories.ProductRepository;
import com.immfly.exceptions.PaymentProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private PaymentService paymentService;
    
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, productRepository, paymentService);
    }

    @Test
    void createOrder_WithValidItems_ShouldCreateOrder() {
        Product product1 = Product.builder()
                .id(1L)
                .name("Test Product 1")
                .price(new BigDecimal("10.00"))
                .build();
        
        Product product2 = Product.builder()
                .id(2L)
                .name("Test Product 2")
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
                .buyerEmail("test@example.com")
                .seatLetter("A")
                .seatNumber(1)
                .items(new ArrayList<>())
                .build();
        order.getItems().add(item1);
        order.getItems().add(item2);

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.createOrder(order);

        assertNotNull(result);
        assertEquals(2, result.getItems().size());
        assertEquals(new BigDecimal("40.00"), result.getTotalPrice());
        assertEquals(OrderStatus.OPEN, result.getStatus());
        assertEquals(PaymentStatus.PAYMENT_FAILED, result.getPaymentStatus());
    }

    @Test
    void createOrder_WithoutSeatPosition_ShouldThrowException() {
        Order order = Order.builder()
                .buyerEmail("test@example.com")
                .items(new ArrayList<>())
                .build();

        assertThrows(IllegalArgumentException.class, () -> 
            orderService.createOrder(order)
        );
    }

    @Test
    void processPayment_WithValidCard_ShouldUpdateOrderStatus() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.OPEN)
                .paymentStatus(PaymentStatus.PAYMENT_FAILED)
                .totalPrice(new BigDecimal("50.00"))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.processPayment(1L, "valid-card-token");

        assertEquals(OrderStatus.FINISHED, result.getStatus());
        assertEquals(PaymentStatus.PAID, result.getPaymentStatus());
        assertNotNull(result.getPaymentDate());
        assertEquals("ONLINE_PAYMENT", result.getPaymentGateway());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void processPayment_WithInvalidCard_ShouldThrowException() throws Exception {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.OPEN)
                .paymentStatus(PaymentStatus.PAYMENT_FAILED)
                .totalPrice(new BigDecimal("50.00"))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doThrow(new Exception("Invalid card")).when(paymentService).processPayment(any(), any());

        assertThrows(PaymentProcessingException.class, () -> 
            orderService.processPayment(1L, "invalid-card-token")
        );
        assertEquals(PaymentStatus.PAYMENT_FAILED, order.getPaymentStatus());
    }

    @Test
    void processPayment_WithAlreadyPaidOrder_ShouldThrowException() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.FINISHED)
                .paymentStatus(PaymentStatus.PAID)
                .totalPrice(new BigDecimal("50.00"))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(PaymentProcessingException.class, () -> 
            orderService.processPayment(1L, "valid-card-token")
        );
    }

    @Test
    void processOfflinePayment_ShouldUpdateOrderStatus() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.OPEN)
                .paymentStatus(PaymentStatus.PAYMENT_FAILED)
                .totalPrice(new BigDecimal("50.00"))
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.processOfflinePayment(1L);

        assertEquals(OrderStatus.FINISHED, result.getStatus());
        assertEquals(PaymentStatus.OFFLINE_PAYMENT, result.getPaymentStatus());
        assertNotNull(result.getPaymentDate());
        assertEquals("OFFLINE_PAYMENT", result.getPaymentGateway());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateOrder_WithNewItems_ShouldRecalculateTotal() {
        Product product1 = Product.builder()
                .id(1L)
                .name("Test Product 1")
                .price(new BigDecimal("10.00"))
                .build();
        
        Product product2 = Product.builder()
                .id(2L)
                .name("Test Product 2")
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

        Order existingOrder = Order.builder()
                .id(1L)
                .buyerEmail("test@example.com")
                .seatLetter("A")
                .seatNumber(1)
                .items(new ArrayList<>())
                .build();
        existingOrder.getItems().add(item1);

        Order updatedOrder = Order.builder()
                .buyerEmail("new@example.com")
                .seatLetter("B")
                .seatNumber(2)
                .items(new ArrayList<>())
                .build();
        updatedOrder.getItems().add(item2);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.updateOrder(1L, updatedOrder);

        assertEquals("new@example.com", result.getBuyerEmail());
        assertEquals("B", result.getSeatLetter());
        assertEquals(2, result.getSeatNumber());
        assertEquals(1, result.getItems().size());
        assertEquals(new BigDecimal("20.00"), result.getTotalPrice());
    }

    @Test
    void updateOrder_WithDuplicateProducts_ShouldThrowException() {
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(new BigDecimal("10.00"))
                .build();

        OrderItem item1 = OrderItem.builder()
                .product(product)
                .quantity(1)
                .build();
                
        OrderItem item2 = OrderItem.builder()
                .product(product)
                .quantity(2)
                .build();

        Order existingOrder = Order.builder()
                .id(1L)
                .buyerEmail("test@example.com")
                .seatLetter("A")
                .seatNumber(1)
                .items(new ArrayList<>())
                .build();

        Order updatedOrder = Order.builder()
                .buyerEmail("test@example.com")
                .seatLetter("A")
                .seatNumber(1)
                .items(new ArrayList<>())
                .build();
        updatedOrder.getItems().add(item1);
        updatedOrder.getItems().add(item2);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(IllegalArgumentException.class, () -> 
            orderService.updateOrder(1L, updatedOrder)
        );
    }
} 
package com.immfly.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private String buyerEmail;
    private String seatLetter;
    private Integer seatNumber;
    private List<OrderItemRequest> items;
} 
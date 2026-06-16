package com.spartaclub.mini.domain.order.dto;

import com.spartaclub.mini.global.common.Status.OrderStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderResponseDto {
    private Long orderId;
    private Long productId;
    private String productName;
    private int productPrice;
    private int quantity;
    private int orderPrice;
    private OrderStatus status;
}

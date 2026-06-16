package com.spartaclub.mini.common.mapper;

import com.spartaclub.mini.domain.order.dto.OrderResponseDto;
import com.spartaclub.mini.domain.order.entity.Order;
import com.spartaclub.mini.domain.product.dto.ProductResponseDto;
import com.spartaclub.mini.domain.product.entity.Product;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EntityDtoMapper {
    public ProductResponseDto toDto(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .status(product.getStatus())
                .build();
    }

    public OrderResponseDto toDto(Order order) {
        return OrderResponseDto.builder()
                .orderId(order.getId())
                .productId(order.getProduct().getId())
                .productName(order.getProduct().getName())
                .productPrice(order.getProduct().getPrice())
                .quantity(order.getQuantity())
                .orderPrice(order.getOrderPrice())
                .status(order.getStatus())
                .build();
    }
}

package com.spartaclub.mini.domain.product.dto;

import com.spartaclub.mini.global.common.Status.ProductStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponseDto {
    private Long id;
    private String name;
    private int price;
    private int stock;
    private ProductStatus status;
}

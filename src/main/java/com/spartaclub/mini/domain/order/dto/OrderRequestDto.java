package com.spartaclub.mini.domain.order.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {
    private Long productId;

    @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
    private int quantity;
}

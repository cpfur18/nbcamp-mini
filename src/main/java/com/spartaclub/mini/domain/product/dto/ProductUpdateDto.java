package com.spartaclub.mini.domain.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDto {
    @NotBlank(message = "상품 명을 입력해주세요.")
    private String name;

    @Min(value = 1, message = "가격은 1원 이상이어야 합니다.")
    private int price;

    @Min(value = 0, message = "재고 수량은 0개 이상이어야 합니다.")
    private int stock;
}

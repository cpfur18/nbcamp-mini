package com.spartaclub.mini.domain.product.entity;

import com.spartaclub.mini.domain.product.dto.ProductCreateDto;
import com.spartaclub.mini.domain.product.dto.ProductUpdateDto;
import com.spartaclub.mini.global.common.BaseTimeEntity;
import com.spartaclub.mini.global.common.Status.ProductStatus;
import com.spartaclub.mini.global.exception.NotEnoughStockException;
import com.spartaclub.mini.global.exception.ProductNotFoundException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "product")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE product SET status = 'DELETED' WHERE product_id = ?")
public class Product extends BaseTimeEntity {
    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.SALE;

    public static Product from(ProductCreateDto request) {
        return Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .stock(request.getStock())
                .build();
    }

    public void update(ProductUpdateDto request) {
        this.name = request.getName();
        this.price = request.getPrice();
        this.stock = request.getStock();

        if (this.stock >= 1) {
            this.status = ProductStatus.SALE;
        } else {
            this.status = ProductStatus.SOLD_OUT;
        }
    }

    public void addStock(int quantity) {
        this.stock += quantity;

        if (this.status == ProductStatus.SOLD_OUT && this.stock >= 1)
            this.status = ProductStatus.SALE;
    }

    public void removeStock(int quantity) {
        if (this.stock < quantity)
            throw new NotEnoughStockException(String.format("해당 상품(id : %d)의 재고가 부족합니다.", this.id));
        this.stock -= quantity;

        if (this.stock == 0) this.status = ProductStatus.SOLD_OUT;
    }

    public void validateNotDeleted() {
        if (this.status == ProductStatus.DELETED) {
            throw new ProductNotFoundException(
                    String.format("해당 상품(id : %d)이 존재하지 않습니다.", this.id));
        }
    }

    public void delete() {
        this.status = ProductStatus.DELETED;
    }
}

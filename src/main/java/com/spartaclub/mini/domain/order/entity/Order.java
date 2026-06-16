package com.spartaclub.mini.domain.order.entity;

import com.spartaclub.mini.common.BaseTimeEntity;
import com.spartaclub.mini.common.Status.OrderStatus;
import com.spartaclub.mini.domain.order.dto.OrderRequestDto;
import com.spartaclub.mini.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Order extends BaseTimeEntity {
    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int orderPrice;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.ORDER;

    public static Order of(Product product, OrderRequestDto request) {
        return Order.builder()
                .product(product)
                .quantity(request.getQuantity())
                .orderPrice(request.getQuantity() * product.getPrice())
                .build();
    }

    public void delete() {
        if (this.status == OrderStatus.CANCEL) {
            throw new IllegalStateException("이미 취소된 주문입니다");
        }
        this.status = OrderStatus.CANCEL;
    }
}

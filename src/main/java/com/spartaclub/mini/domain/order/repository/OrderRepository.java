package com.spartaclub.mini.domain.order.repository;

import com.spartaclub.mini.domain.order.dto.OrderResponseDto;
import com.spartaclub.mini.domain.order.entity.Order;
import com.spartaclub.mini.global.exception.OrderNotFoundException;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"product"})
    Optional<Order> findWithProductById(Long orderId);

    default Order findWithProductByIdOrThrow(Long orderId) {
        return findWithProductById(orderId)
                .orElseThrow(
                        () ->
                                new OrderNotFoundException(
                                        String.format("해당 주문(id : %d)이 존재하지 않습니다.", orderId)));
    }

    @Query(
            value =
                    "SELECT new com.spartaclub.mini.domain.order.dto.OrderResponseDto(o.id, p.id,"
                        + " p.name, p.price, o.quantity, o.orderPrice, o.status) FROM Order o JOIN"
                        + " o.product p",
            countQuery = "SELECT count(o) FROM Order o")
    Page<OrderResponseDto> findAllWithProduct(Pageable pageable);
}

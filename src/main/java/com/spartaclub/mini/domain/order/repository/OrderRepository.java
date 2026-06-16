package com.spartaclub.mini.domain.order.repository;

import com.spartaclub.mini.domain.order.dto.OrderResponseDto;
import com.spartaclub.mini.domain.order.entity.Order;
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

    @Query(
            value =
                    "SELECT new com.spartaclub.mini.domain.order.dto.OrderResponseDto(o.id, p.id,"
                        + " p.name, p.price, o.quantity, o.orderPrice, o.status) FROM Order o JOIN"
                        + " o.product p",
            countQuery = "SELECT count(o) FROM Order o")
    Page<OrderResponseDto> findAllWithProduct(Pageable pageable);
}

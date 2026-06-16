package com.spartaclub.mini.domain.product.repository;

import com.spartaclub.mini.common.Status.ProductStatus;
import com.spartaclub.mini.domain.product.entity.Product;
import com.spartaclub.mini.global.exception.ProductNotFoundException;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByIdAndStatusNot(Long productId, ProductStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> findByIdForUpdate(Long productId);

    default Product findNotDeletedOrThrow(Long productId) {
        return findByIdAndStatusNot(productId, ProductStatus.DELETED)
                .orElseThrow(
                        () ->
                                new ProductNotFoundException(
                                        String.format("해당 상품(id : %d)이 존재하지 않습니다.", productId)));
    }

    default Product findByIdForUpdateOrThrow(Long productId) {
        return findByIdForUpdate(productId)
                .orElseThrow(
                        () ->
                                new ProductNotFoundException(
                                        String.format("해당 상품(id : %d)이 존재하지 않습니다.", productId)));
    }

    List<Product> findAllByStatusNot(ProductStatus status);
}

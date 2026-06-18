package com.spartaclub.mini.domain.product;

import com.spartaclub.mini.domain.product.dto.ProductCreateDto;
import com.spartaclub.mini.domain.product.dto.ProductResponseDto;
import com.spartaclub.mini.domain.product.dto.ProductUpdateDto;
import com.spartaclub.mini.domain.product.entity.Product;
import com.spartaclub.mini.domain.product.repository.ProductRepository;
import com.spartaclub.mini.global.common.Status.ProductStatus;
import com.spartaclub.mini.global.common.mapper.EntityDtoMapper;
import com.spartaclub.mini.global.exception.ProductDuplicatedNameException;
import com.spartaclub.mini.global.exception.ProductNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResponseDto createProduct(ProductCreateDto request) {
        if (productRepository.existsByName(request.getName())) {
            throw new ProductDuplicatedNameException(
                    String.format("중복된 상품명(name : %s) 입니다.", request.getName()));
        }

        Product savedProduct = productRepository.save(Product.from(request));

        return EntityDtoMapper.toDto(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(Long productId) {
        Product product =
                productRepository
                        .findByIdAndStatusNot(productId, ProductStatus.DELETED)
                        .orElseThrow(
                                () ->
                                        new ProductNotFoundException(
                                                String.format(
                                                        "해당 상품(id : %d)이 존재하지 않습니다.", productId)));

        return EntityDtoMapper.toDto(product);
    }

    @Transactional(readOnly = true)
    public Product findProduct(Long productId) {
        return productRepository
                .findByIdForUpdate(productId)
                .orElseThrow(
                        () ->
                                new ProductNotFoundException(
                                        String.format("해당 상품(id : %d)이 존재하지 않습니다.", productId)));
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductList() {
        var productList = productRepository.findAllByStatusNot(ProductStatus.DELETED);

        return productList.stream().map(EntityDtoMapper::toDto).toList();
    }

    public ProductResponseDto updateProduct(ProductUpdateDto request, Long productId) {
        Product product =
                productRepository
                        .findByIdAndStatusNot(productId, ProductStatus.DELETED)
                        .orElseThrow(
                                () ->
                                        new ProductNotFoundException(
                                                String.format(
                                                        "해당 상품(id : %d)이 존재하지 않습니다.", productId)));
        product.update(request);

        return EntityDtoMapper.toDto(product);
    }

    public void deleteProduct(Long productId) {
        Product product =
                productRepository
                        .findByIdAndStatusNot(productId, ProductStatus.DELETED)
                        .orElseThrow(
                                () ->
                                        new ProductNotFoundException(
                                                String.format(
                                                        "해당 상품(id : %d)이 존재하지 않습니다.", productId)));
        product.delete();
    }
}

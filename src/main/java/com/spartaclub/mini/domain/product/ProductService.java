package com.spartaclub.mini.domain.product;

import com.spartaclub.mini.common.Status.ProductStatus;
import com.spartaclub.mini.common.mapper.EntityDtoMapper;
import com.spartaclub.mini.domain.product.dto.ProductCreateDto;
import com.spartaclub.mini.domain.product.dto.ProductResponseDto;
import com.spartaclub.mini.domain.product.dto.ProductUpdateDto;
import com.spartaclub.mini.domain.product.entity.Product;
import com.spartaclub.mini.domain.product.repository.ProductRepository;
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
        Product savedProduct = productRepository.save(Product.from(request));

        return EntityDtoMapper.toDto(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(Long productId) {
        Product product = productRepository.findNotDeletedOrThrow(productId);

        return EntityDtoMapper.toDto(product);
    }

    @Transactional(readOnly = true)
    public Product findProduct(Long productId) {
        return productRepository.findByIdForUpdateOrThrow(productId);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductList() {
        var productList = productRepository.findAllByStatusNot(ProductStatus.DELETED);

        return productList.stream().map(EntityDtoMapper::toDto).toList();
    }

    public ProductResponseDto updateProduct(ProductUpdateDto request, Long productId) {
        Product product = productRepository.findNotDeletedOrThrow(productId);

        product.update(request);
        return EntityDtoMapper.toDto(product);
    }

    public void deleteProduct(Long productId) {
        Product product = productRepository.findNotDeletedOrThrow(productId);

        product.delete();
    }
}

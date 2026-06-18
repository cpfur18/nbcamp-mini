package com.spartaclub.mini.domain.product;

import com.spartaclub.mini.domain.product.dto.ProductCreateDto;
import com.spartaclub.mini.domain.product.dto.ProductResponseDto;
import com.spartaclub.mini.domain.product.dto.ProductUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@Tag(name = "products", description = "상품 API")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @Operation(summary = "상품 등록", description = "새로운 상품을 등록한다.")
    @ApiResponse(responseCode = "201", description = "상품 등록 성공")
    public ResponseEntity<ProductResponseDto> createProduct(
            @RequestBody @Valid ProductCreateDto request) {
        ProductResponseDto response = productService.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 조회한다.")
    @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        var ProductList = productService.getProductList();

        return ResponseEntity.ok(ProductList);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "상품 단건 조회", description = "특정 상품을 조회한다.")
    @ApiResponse(responseCode = "200", description = "상품 조회 성공")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long productId) {
        ProductResponseDto response = productService.getProduct(productId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "상품 수정", description = "특정 상품를 수정한다.")
    @ApiResponse(responseCode = "200", description = "상품 수정 성공")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @RequestBody @Valid ProductUpdateDto request, @PathVariable Long productId) {
        ProductResponseDto response = productService.updateProduct(request, productId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "상품 삭제", description = "특정 상품를 삭제한다.")
    @ApiResponse(responseCode = "204", description = "상품 삭제 성공")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);

        return ResponseEntity.noContent().build();
    }
}

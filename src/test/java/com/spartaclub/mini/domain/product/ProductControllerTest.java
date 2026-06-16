package com.spartaclub.mini.domain.product;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spartaclub.mini.common.Status.ProductStatus;
import com.spartaclub.mini.domain.product.dto.ProductCreateDto;
import com.spartaclub.mini.domain.product.dto.ProductResponseDto;
import com.spartaclub.mini.domain.product.dto.ProductUpdateDto;
import com.spartaclub.mini.global.exception.ProductNotFoundException;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private ProductService productService;

    @Nested
    @DisplayName("상품 생성 테스트")
    class CreateProduct {
        @Test
        @DisplayName("상품 생성 성공")
        void createProduct_success() throws Exception {
            // given
            Long productId = 1L;
            ProductCreateDto request = new ProductCreateDto("노트북", 1000, 10);
            ProductResponseDto response = createResponseDto(productId, request, ProductStatus.SALE);

            given(productService.createProduct(any(ProductCreateDto.class))).willReturn(response);
            // when & then
            mockMvc.perform(
                            post("/api/v1/products")
                                    .contentType("application/json")
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("노트북"))
                    .andExpect(jsonPath("$.price").value(1000))
                    .andExpect(jsonPath("$.stock").value(10))
                    .andExpect(jsonPath("$.status").value(ProductStatus.SALE.toString()));
        }

        @MethodSource("createRequest")
        @ParameterizedTest(name = "{1}")
        @DisplayName("상품 생성 시 Valid 검증 실패")
        void createProduct_fail_when_validation_fail(
                ProductCreateDto request, String expectedMessage) throws Exception {
            // when & then
            mockMvc.perform(
                            post("/api/v1/products")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(expectedMessage));
        }

        static Stream<Arguments> createRequest() {
            return Stream.of(
                    Arguments.of(new ProductCreateDto("", 1000, 10), "상품 명을 입력해주세요."),
                    Arguments.of(new ProductCreateDto("노트북", 0, 10), "가격은 1원 이상이어야 합니다."),
                    Arguments.of(new ProductCreateDto("노트북", 1000, 0), "재고 수량은 1개 이상이어야 합니다."));
        }
    }

    @Nested
    @DisplayName("상품 조회 테스트")
    class FindProduct {
        @Test
        @DisplayName("상품 목록 조회 성공")
        void getAllProducts_success() throws Exception {
            // given
            ProductCreateDto request = new ProductCreateDto("노트북", 1000, 10);
            ProductCreateDto request2 = new ProductCreateDto("스마트폰", 2000, 20);

            List<ProductResponseDto> responseList =
                    List.of(
                            createResponseDto(1L, request, ProductStatus.SALE),
                            createResponseDto(2L, request2, ProductStatus.SALE));

            given(productService.getProductList()).willReturn(responseList);

            // when & then
            mockMvc.perform(get("/api/v1/products").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].name").value("노트북"))
                    .andExpect(jsonPath("$[0].price").value(1000))
                    .andExpect(jsonPath("$[0].stock").value(10))
                    .andExpect(jsonPath("$[0].status").value(ProductStatus.SALE.toString()))
                    .andExpect(jsonPath("$[1].id").value(2L))
                    .andExpect(jsonPath("$[1].name").value("스마트폰"))
                    .andExpect(jsonPath("$[1].price").value(2000))
                    .andExpect(jsonPath("$[1].stock").value(20))
                    .andExpect(jsonPath("$[1].status").value(ProductStatus.SALE.toString()));
        }

        @Test
        @DisplayName("ID로 상품 조회 성공")
        void getProductById_success() throws Exception {
            // given
            Long productId = 1L;
            ProductCreateDto request = new ProductCreateDto("노트북", 1000, 10);
            ProductResponseDto response = createResponseDto(productId, request, ProductStatus.SALE);

            given(productService.getProduct(eq(productId))).willReturn(response);

            // when & then
            mockMvc.perform(
                            get("/api/v1/products/{id}", productId)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(productId))
                    .andExpect(jsonPath("$.name").value("노트북"))
                    .andExpect(jsonPath("$.price").value(1000))
                    .andExpect(jsonPath("$.stock").value(10))
                    .andExpect(jsonPath("$.status").value(ProductStatus.SALE.toString()));
        }

        @Test
        @DisplayName("존재하지 않는 상품 조회 시 예외 발생해야한다.")
        void getProductById_fail_when_notFound() throws Exception {
            // given
            Long productId = 1L;
            given(productService.getProduct(eq(productId)))
                    .willThrow(
                            new ProductNotFoundException(
                                    String.format("해당 상품(id: %d)이 존재하지 않습니다.", productId)));

            // when & then
            mockMvc.perform(
                            get("/api/v1/products/{id}", productId)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("상품을 찾을 수 없습니다."));
        }
    }

    @Nested
    @DisplayName("상품 수정 테스트")
    class UpdateProduct {
        @Test
        @DisplayName("상품 수정 성공")
        void updateProduct_success() throws Exception {
            // given
            Long productId = 1L;
            ProductUpdateDto updateRequest = new ProductUpdateDto("스마트폰", 1000, 0);

            ProductResponseDto response =
                    ProductResponseDto.builder()
                            .id(productId)
                            .name(updateRequest.getName())
                            .price(updateRequest.getPrice())
                            .stock(updateRequest.getStock())
                            .status(ProductStatus.SOLD_OUT)
                            .build();

            given(productService.updateProduct(any(ProductUpdateDto.class), eq(productId)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(
                            put("/api/v1/products/{id}", productId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(productId))
                    .andExpect(jsonPath("$.name").value("스마트폰"))
                    .andExpect(jsonPath("$.price").value(1000))
                    .andExpect(jsonPath("$.stock").value(0))
                    .andExpect(jsonPath("$.status").value(ProductStatus.SOLD_OUT.toString()));
        }

        @Test
        @DisplayName("존재하지 않는 상품 수정 시 예외가 발생해야한다.")
        void updateProduct_fail_when_notFound() throws Exception {
            // given
            Long productId = 1L;
            ProductUpdateDto updateRequest = new ProductUpdateDto("스마트폰", 1000, 10);

            given(productService.updateProduct(any(ProductUpdateDto.class), eq(productId)))
                    .willThrow(
                            new ProductNotFoundException(
                                    String.format("해당 상품(id: %d)이 존재하지 않습니다.", productId)));

            // when & then
            mockMvc.perform(
                            put("/api/v1/products/{id}", productId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("상품을 찾을 수 없습니다."));
        }
    }

    @Nested
    @DisplayName("상품 삭제 테스트")
    class DeleteProduct {
        @Test
        @DisplayName("상품 삭제 성공")
        void deleteProduct_success() throws Exception {
            // given
            Long productId = 1L;

            // when & then
            mockMvc.perform(
                            delete("/api/v1/products/{id}", productId)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("존재하지 않는 상품 상제 시 예외 발생")
        void deleteProduct_fail_when_notFound() throws Exception {
            // given
            Long productId = 1L;

            willThrow(
                            new ProductNotFoundException(
                                    String.format("해당 상품(id: %d)이 존재하지 않습니다.", productId)))
                    .given(productService)
                    .deleteProduct(eq(productId));

            // when & then
            mockMvc.perform(
                            delete("/api/v1/products/{id}", productId)
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("상품을 찾을 수 없습니다."));
        }
    }

    private ProductResponseDto createResponseDto(
            Long productId, ProductCreateDto request, ProductStatus status) {
        return ProductResponseDto.builder()
                .id(productId)
                .name(request.getName())
                .price(request.getPrice())
                .stock(request.getStock())
                .status(status)
                .build();
    }
}

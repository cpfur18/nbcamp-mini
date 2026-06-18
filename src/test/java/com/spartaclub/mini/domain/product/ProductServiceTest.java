package com.spartaclub.mini.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.spartaclub.mini.domain.product.dto.ProductCreateDto;
import com.spartaclub.mini.domain.product.dto.ProductResponseDto;
import com.spartaclub.mini.domain.product.dto.ProductUpdateDto;
import com.spartaclub.mini.domain.product.repository.ProductRepository;
import com.spartaclub.mini.global.exception.ProductDuplicatedNameException;
import com.spartaclub.mini.global.exception.ProductNotFoundException;
import com.spartaclub.mini.testconfig.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class ProductServiceTest extends AbstractIntegrationTest {
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductService productService;

    @Nested
    @DisplayName("상품 생성 테스트")
    class createProduct {
        @Test
        @Transactional
        @DisplayName("상품 생성 성공")
        void createProduct_success() {
            // given
            ProductCreateDto request = new ProductCreateDto("노트북", 1000, 10);

            // when
            ProductResponseDto savedProduct = productService.createProduct(request);

            // then
            assertProduct(savedProduct, request);
        }

        @Test
        @Transactional
        @DisplayName("상품 생성 시 동일한 Name이 있는 경우 예외 처리가 되어야 한다.")
        void createProduct_fail_when_duplicated_name() {
            // given
            ProductCreateDto request = new ProductCreateDto("노트북", 1000, 10);
            ProductResponseDto savedProduct = productService.createProduct(request);

            // when & then
            assertThatThrownBy(() -> productService.createProduct(request))
                    .isInstanceOf(ProductDuplicatedNameException.class)
                    .hasMessage("중복된 상품명(name : %s) 입니다.", request.getName());
        }
    }

    @Nested
    @DisplayName("상품 조회 테스트")
    class getProduct {
        @Test
        @Transactional
        @DisplayName("상품 목록 조회 성공")
        void getProductList_success() {
            // given
            ProductCreateDto request1 = new ProductCreateDto("노트북", 1000, 10);
            ProductCreateDto request2 = new ProductCreateDto("모니터", 500, 20);

            ProductResponseDto savedResponse1 = productService.createProduct(request1);
            ProductResponseDto savedResponse2 = productService.createProduct(request2);

            // when
            var response = productService.getProductList();

            // then
            assertThat(response).hasSize(2);

            assertThat(response.get(0).getId()).isEqualTo(savedResponse1.getId());
            assertThat(response.get(1).getId()).isEqualTo(savedResponse2.getId());

            assertProduct(response.get(0), request1);
            assertProduct(response.get(1), request2);
        }

        @Test
        @Transactional
        @DisplayName("상품 조회 성공")
        void getProductById_success() {
            // given
            ProductCreateDto request = new ProductCreateDto("노트북", 1000, 10);

            ProductResponseDto savedProduct = productService.createProduct(request);

            // when
            ProductResponseDto response = productService.getProduct(savedProduct.getId());

            // then
            assertProduct(response, request);
        }

        @Test
        @Transactional
        @DisplayName("삭제된 상품 조회 시 예외가 발생해야 한다.")
        void getProductById_fail_when_deleted() {
            // given
            ProductCreateDto request = new ProductCreateDto("노트북", 1000, 10);

            ProductResponseDto savedProduct = productService.createProduct(request);
            productService.deleteProduct(savedProduct.getId());

            // when & then
            assertThatThrownBy(() -> productService.getProduct(savedProduct.getId()))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessage("해당 상품(id : %d)이 존재하지 않습니다.", savedProduct.getId());
        }

        @Test
        @Transactional
        @DisplayName("존재하지 않는 상품 조회 시 예외가 발생해야 한다.")
        void getProductById_fail_when_notFound() {
            // given
            Long productId = 1L;

            // when & then
            assertThatThrownBy(() -> productService.getProduct(productId))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessage("해당 상품(id : %d)이 존재하지 않습니다.", productId);
        }
    }

    @Nested
    @DisplayName("상품 수정 테스트")
    class UpdateProduct {
        @Test
        @Transactional
        @DisplayName("상품 수정 성공")
        void updateProduct_success() {
            // given
            ProductCreateDto request = new ProductCreateDto("노트북", 1000, 10);
            ProductUpdateDto updateRequest = new ProductUpdateDto("노트북2", 1500, 10);

            ProductResponseDto savedResponse = productService.createProduct(request);

            // when
            ProductResponseDto updateResponse =
                    productService.updateProduct(updateRequest, savedResponse.getId());

            // then
            assertThat(updateResponse.getName()).isEqualTo(updateRequest.getName());
            assertThat(updateResponse.getPrice()).isEqualTo(updateRequest.getPrice());
            assertThat(updateResponse.getStock()).isEqualTo(updateRequest.getStock());
        }

        @Test
        @Transactional
        @DisplayName("삭제된 상품 수정 시 예외가 발생해야 한다.")
        void updateProduct_fail_when_deleted() {
            // given
            ProductCreateDto request = new ProductCreateDto("노트북", 1000, 10);
            ProductUpdateDto updateRequest = new ProductUpdateDto("노트북2", 1500, 10);

            ProductResponseDto savedResponse = productService.createProduct(request);

            productService.deleteProduct(savedResponse.getId());

            // when & then
            assertThatThrownBy(
                            () ->
                                    productService.updateProduct(
                                            updateRequest, savedResponse.getId()))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessage("해당 상품(id : %d)이 존재하지 않습니다.", savedResponse.getId());
        }

        @Test
        @Transactional
        @DisplayName("존재하지 않는 상품 수정 시 예외가 발생해야 한다.")
        void updateProduct_fail_when_notFound() {
            // given
            Long productId = 3L;
            ProductUpdateDto updateRequest = new ProductUpdateDto("노트북2", 1500, 10);

            // when & then
            assertThatThrownBy(() -> productService.updateProduct(updateRequest, productId))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessage("해당 상품(id : %d)이 존재하지 않습니다.", productId);
        }
    }

    @Nested
    @DisplayName("상품 삭제 테스트")
    class DeleteProduct {
        @Test
        @Transactional
        @DisplayName("상품 삭제 성공")
        void deleteProduct_success() {
            // given
            ProductCreateDto request = new ProductCreateDto("노트북", 1000, 10);
            ProductResponseDto savedResponse = productService.createProduct(request);

            // when
            productService.deleteProduct(1L);

            // then
            assertThatThrownBy(() -> productService.getProduct(savedResponse.getId()))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessage("해당 상품(id : %d)이 존재하지 않습니다.", savedResponse.getId());
        }

        @Test
        @Transactional
        @DisplayName("Repository 에서 직접 delete 시 @SQLDelete로 인해 UPDATE가 되어야 한다.")
        void deleteProduct_success_repository_delete_test() {
            // given
            ProductCreateDto request = new ProductCreateDto("노트북", 1000, 10);
            ProductResponseDto savedProduct = productService.createProduct(request);

            // when
            productRepository.deleteById(savedProduct.getId());

            // then
            assertThatThrownBy(() -> productService.getProduct(savedProduct.getId()))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessage("해당 상품(id : %d)이 존재하지 않습니다.", savedProduct.getId());
        }

        @Test
        @Transactional
        @DisplayName("존재하지 않는 상품 삭제 시 예외가 발생해야 한다.")
        void deleteProduct_fail_when_notFound() {
            // given
            Long productId = 3L;

            // when & then
            assertThatThrownBy(() -> productService.deleteProduct(productId))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessage("해당 상품(id : %d)이 존재하지 않습니다.", productId);
        }
    }

    private void assertProduct(ProductResponseDto response, ProductCreateDto request) {
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getPrice()).isEqualTo(request.getPrice());
        assertThat(response.getStock()).isEqualTo(request.getStock());
    }
}

package com.spartaclub.mini.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.spartaclub.mini.domain.order.dto.OrderRequestDto;
import com.spartaclub.mini.domain.order.dto.OrderResponseDto;
import com.spartaclub.mini.domain.product.ProductService;
import com.spartaclub.mini.domain.product.entity.Product;
import com.spartaclub.mini.domain.product.repository.ProductRepository;
import com.spartaclub.mini.global.common.Status.OrderStatus;
import com.spartaclub.mini.global.common.Status.ProductStatus;
import com.spartaclub.mini.global.exception.NotEnoughStockException;
import com.spartaclub.mini.global.exception.OrderNotFoundException;
import com.spartaclub.mini.global.exception.ProductNotFoundException;
import com.spartaclub.mini.testconfig.DatabaseTestSupport;
import com.spartaclub.mini.testutil.ConcurrencyTestingUtil;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class OrderServiceTest extends DatabaseTestSupport {
    @Autowired OrderService orderService;
    @Autowired ProductService productService;
    @Autowired ProductRepository productRepository;

    Product dummyProduct;
    Product dummyProduct2;

    @BeforeEach
    void setUp() {
        dummyProduct =
                productRepository.save(
                        Product.builder().name("dummy").price(100).stock(10).build());

        dummyProduct2 =
                productRepository.save(
                        Product.builder().name("dummy2").price(200).stock(20).build());

        OrderRequestDto request = new OrderRequestDto(1L, 1);
        orderService.createOrder(request);
    }

    @Nested
    @DisplayName("주문 생성 테스트")
    class CreateOrder {
        @Test
        @Transactional
        @DisplayName("주문 생성 성공.")
        void createOrder_success() {
            // given
            Long productId = 2L;
            OrderRequestDto request = new OrderRequestDto(productId, 1);

            // when
            OrderResponseDto response = orderService.createOrder(request);

            // then
            assertOrder(response, request, dummyProduct2);
        }

        @Test
        @Transactional
        @DisplayName("상품이 없을 경우 주문 생성 시 예외가 발생해야 한다.")
        void createOrder_fail_valid_id() {
            // given
            Long productId = 3L;
            OrderRequestDto request = new OrderRequestDto(productId, 1);
            // when & then
            assertThatThrownBy(() -> orderService.createOrder(request))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessage(String.format("해당 상품(id : %d)이 존재하지 않습니다.", 3L));
        }

        @Test
        @Transactional
        @DisplayName("재고가 부족한 경우 주문 생성 시 예외가 발생해야 한다.")
        void createOrder_fail_when_stock_insufficient() {
            // given
            Long productId = 2L;
            OrderRequestDto request = new OrderRequestDto(productId, 21);

            // when & then
            assertThatThrownBy(() -> orderService.createOrder(request))
                    .isInstanceOf(NotEnoughStockException.class)
                    .hasMessage(String.format("해당 상품(id : %d)의 재고가 부족합니다.", dummyProduct2.getId()));
        }

        @Test
        @DisplayName("주문 생성 시 동시 클릭으로 인해 재고가 부족할 경우 예외가 발생해야한다.")
        void createOrder_fail_when_stock_not_enough() throws InterruptedException {
            // given
            Long productId = 2L;
            int threadCount = 3;

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failureCount = new AtomicInteger(0);

            // when
            ConcurrencyTestingUtil.run(
                    threadCount,
                    () -> {
                        try {
                            // NOTE : 미리 설정한 dummyProduct의 재고가 20개이므로 10개씩 2회 성공, 1회 실패해야한다.
                            orderService.createOrder(new OrderRequestDto(productId, 10));
                            successCount.incrementAndGet();
                        } catch (NotEnoughStockException e) {
                            failureCount.incrementAndGet();
                        }
                    });

            // then
            assertThat(successCount.get()).isEqualTo(2);
            assertThat(failureCount.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("주문 생성 후 재고가 차감 되어야 한다.")
        void createOrder_success_stock_reduced() {
            // given
            Long productId = 2L;
            OrderRequestDto request = new OrderRequestDto(productId, 5);

            // when
            orderService.createOrder(request);
            int productStock = productService.getProduct(request.getProductId()).getStock();

            // then
            assertThat(productStock).isEqualTo(15);
        }

        @Test
        @DisplayName("주문 생성으로 인한 재고 차감 후 재고가 없으면 Product가 SOLD_OUT 상태로 바뀌어야 한다")
        void createOrder_success_stock_sold_out() {
            // given
            Long productId = 2L;
            OrderRequestDto request = new OrderRequestDto(productId, 20);

            // when
            OrderResponseDto response = orderService.createOrder(request);
            ProductStatus status = productService.getProduct(request.getProductId()).getStatus();

            // then
            assertThat(status).isEqualTo(ProductStatus.SOLD_OUT);
        }
    }

    @Nested
    @DisplayName("주문 조회 테스트")
    class GetOrder {
        @Test
        @Transactional
        @DisplayName("주문 목록 조회 성공")
        void getOrderList_success() {
            // given
            Long productId = 2L;
            Pageable pageable = PageRequest.of(0, 5);
            OrderRequestDto request = new OrderRequestDto(productId, 1);

            // when
            for (int i = 0; i < 10; i++) orderService.createOrder(request);

            var orderList = orderService.getOrderList(pageable);

            // then
            assertThat(orderList.getContent()).hasSize(5);
            assertThat(orderList.getTotalElements()).isEqualTo(11);
            assertThat(orderList.getTotalPages()).isEqualTo(3);
        }

        @Test
        @Transactional
        @DisplayName("주문 ID로 주문 조회 성공")
        void getOrder_success() {
            // given
            Long orderId = 1L;
            Long productId = 1L;
            OrderRequestDto request = new OrderRequestDto(productId, 1);

            // when & then
            assertOrder(orderService.getOrder(orderId), request, dummyProduct);
        }

        @Test
        @Transactional
        @DisplayName("존재하지 않는 주문 ID로 조회 시 예외가 발생해야 한다")
        void getOrder_fail_valid_id() {
            // given
            Long orderId = 3L;

            // when & then
            assertThatThrownBy(() -> orderService.getOrder(orderId))
                    .isInstanceOf(OrderNotFoundException.class)
                    .hasMessage(String.format("해당 주문(id : %d)이 존재하지 않습니다.", orderId));
        }
    }

    @Nested
    @DisplayName("주문 취소 테스트")
    class DeleteOrder {
        @Test
        @Transactional
        @DisplayName("주문 ID를 입력 받아 주문을 삭제 시 CANCEL 상태로 변경되어야 한다.")
        void deleteOrder_success() {
            // given
            Long orderId = 1L;

            // when
            orderService.deleteOrder(orderId);
            OrderResponseDto response = orderService.getOrder(orderId);

            // then
            assertThat(response.getStatus()).isEqualTo(OrderStatus.CANCEL);
        }

        @Test
        @Transactional
        @DisplayName("주문 취소 시 주문 수량만큼 재고가 다시 증가해야 한다.")
        void deleteOrder_success_stock_increased() {
            // given
            Long orderId = 1L;
            Long productId = 1L;

            // when
            orderService.deleteOrder(orderId);
            int productStock = productService.getProduct(productId).getStock();

            // then
            assertThat(productStock).isEqualTo(10);
        }
    }

    private void assertOrder(OrderResponseDto response, OrderRequestDto request, Product product) {
        assertThat(response.getProductId()).isEqualTo(request.getProductId());
        assertThat(response.getProductName()).isEqualTo(product.getName());
        assertThat(response.getProductPrice()).isEqualTo(product.getPrice());
        assertThat(response.getQuantity()).isEqualTo(request.getQuantity());
    }
}

// package com.spartaclub.mini.domain.order;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.spartaclub.mini.domain.order.dto.OrderRequestDto;
// import com.spartaclub.mini.domain.order.dto.OrderResponseDto;
// import com.spartaclub.mini.global.common.Status.OrderStatus;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.test.context.bean.override.mockito.MockitoBean;
// import org.springframework.test.web.servlet.MockMvc;
//
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.BDDMockito.given;
//
// @WebMvcTest(OrderController.class)
// class OrderControllerTest {
//    @Autowired private MockMvc mockMvc;
//    @Autowired private ObjectMapper objectMapper;
//    @MockitoBean private OrderService orderService;
//
//    @Nested
//    @DisplayName("주문 등록 테스트")
//    class CreateOrder {
//        @Test
//        @DisplayName("주문 등록 성공")
//        void createOrder_success()  {
//            // given
//            Long orderId = 1L;
//            Long productId = 1L;
//
//            OrderRequestDto request = new OrderRequestDto(productId, 1);
//            OrderResponseDto response =
//                    OrderResponseDto.builder()
//                            .orderId(orderId)
//                            .productId(request.getProductId())
//                            .productName("test")
//                            .quantity(request.getQuantity())
//                            .orderPrice(100)
//                            .status(OrderStatus.ORDER)
//                            .build();
//
//            given(orderService.createOrder(any(OrderRequestDto.class))).willReturn(response);
//
//            // when & then
//            mockMvc.perform(
//                    post("")
//            )
//
//        }
//
//        @Test
//        @DisplayName("주문 등록 시 재고 수량 1개 미만일 때 Valid 검증 실패")
//        void createOrder_fail_when_no_stock()  {
//
//        }
//    }
//
//    @Nested
//    @DisplayName("주문 조회 테스트")
//    class GetOrder {
//
//
//    }
//
//
//    @Test
//    void getAllOrders() {
//    }
//
//    @Test
//    void getOrder() {
//    }
//
//    @Test
//    void cancelOrder() {
//    }
// }

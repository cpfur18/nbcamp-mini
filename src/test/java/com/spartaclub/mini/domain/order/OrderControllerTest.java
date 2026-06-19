package com.spartaclub.mini.domain.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spartaclub.mini.domain.order.dto.OrderRequestDto;
import com.spartaclub.mini.domain.order.dto.OrderResponseDto;
import com.spartaclub.mini.global.common.Status.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private OrderService orderService;

    @Nested
    @DisplayName("주문 등록 테스트")
    class CreateOrder {
        @Test
        @DisplayName("주문 등록 성공")
        void createOrder_success() throws Exception {
            // given
            Long orderId = 1L;
            Long productId = 1L;

            OrderRequestDto request = new OrderRequestDto(productId, 1);
            OrderResponseDto response =
                    OrderResponseDto.builder()
                            .orderId(orderId)
                            .productId(request.getProductId())
                            .productName("test")
                            .quantity(request.getQuantity())
                            .orderPrice(100)
                            .status(OrderStatus.ORDER)
                            .build();

            given(orderService.createOrder(any(OrderRequestDto.class))).willReturn(response);

            // when & then
            mockMvc.perform(
                            post("/api/v1/orders")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.orderId").value(orderId))
                    .andExpect(jsonPath("$.productId").value(request.getProductId()))
                    .andExpect(jsonPath("$.productName").value("test"))
                    .andExpect(jsonPath("$.quantity").value(request.getQuantity()))
                    .andExpect(jsonPath("$.orderPrice").value(100))
                    .andExpect(jsonPath("$.status").value(OrderStatus.ORDER.toString()));
        }

        @Test
        @DisplayName("주문 등록 시 재고 수량 1개 미만일 때 Valid 검증 실패")
        void createOrder_fail_when_no_stock() throws Exception {
            // given
            Long productId = 1L;
            OrderRequestDto request = new OrderRequestDto(productId, 0);

            // when & then
            mockMvc.perform(
                            post("/api/v1/orders")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    //    @Nested
    //    @DisplayName("주문 조회 테스트")
    //    class GetOrder {
    //        @Test
    //        @DisplayName()
    //        getOrder_success() {
    //
    //        }
    //
    //        @Test
    //        @DisplayName()
    //        getAllOrders_success() {
    //
    //        }
    //
    //    }
    //
    //    @Test
    //    void cancelOrder() {
    //    }
}

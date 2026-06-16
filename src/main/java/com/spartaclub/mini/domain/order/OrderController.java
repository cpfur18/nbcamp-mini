package com.spartaclub.mini.domain.order;

import com.spartaclub.mini.domain.order.dto.OrderRequestDto;
import com.spartaclub.mini.domain.order.dto.OrderResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "주문 생성", description = "새로운 주문을 생성한다.")
    @ApiResponse(responseCode = "200", description = "주문 생성 성공")
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody OrderRequestDto orderRequestDto) {
        OrderResponseDto response = orderService.createOrder(orderRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "주문 목록 조회", description = "모든 주문 정보를 조회한다.")
    @ApiResponse(responseCode = "200", description = "주문 목록 조회 성공")
    public ResponseEntity<Page<OrderResponseDto>> getAllOrders(Pageable pageable) {
        var response = orderService.getOrderList(pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "주문 조회", description = "주문 ID로 주문 정보를 조회한다.")
    @ApiResponse(responseCode = "200", description = "주문 조회 성공")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long orderId) {
        OrderResponseDto response = orderService.getOrder(orderId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "주문 취소", description = "주문 ID로 주문을 취소한다.")
    @ApiResponse(responseCode = "204", description = "주문 취소 성공")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);

        return ResponseEntity.noContent().build();
    }
}

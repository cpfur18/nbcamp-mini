package com.spartaclub.mini.domain.order;

import com.spartaclub.mini.common.mapper.EntityDtoMapper;
import com.spartaclub.mini.domain.order.dto.OrderRequestDto;
import com.spartaclub.mini.domain.order.dto.OrderResponseDto;
import com.spartaclub.mini.domain.order.entity.Order;
import com.spartaclub.mini.domain.order.repository.OrderRepository;
import com.spartaclub.mini.domain.product.ProductService;
import com.spartaclub.mini.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;

    public OrderResponseDto createOrder(OrderRequestDto request) {
        Product product = productService.findProduct(request.getProductId());
        Order savedOrder = orderRepository.save(Order.of(product, request));

        product.validateNotDeleted();
        product.removeStock(request.getQuantity());

        return EntityDtoMapper.toDto(
                orderRepository.findWithProductByIdOrThrow(savedOrder.getId()));
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(Long orderId) {
        Order order = orderRepository.findWithProductByIdOrThrow(orderId);

        return EntityDtoMapper.toDto(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrderList(Pageable pageable) {
        return orderRepository.findAllWithProduct(pageable);
    }

    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findWithProductByIdOrThrow(orderId);

        order.delete();
        order.getProduct().addStock(order.getQuantity());
    }
}

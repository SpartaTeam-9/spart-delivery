package com.sparta.spartadelivery.order.application;


import com.sparta.spartadelivery.order.domain.entity.OrderStatus;
import com.sparta.spartadelivery.order.domain.repository.OrderRepository;
import com.sparta.spartadelivery.order.presentation.dto.request.CreateOrderRequest;
import com.sparta.spartadelivery.order.presentation.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    // private final OrderItemUseCase orderItemUsecase;

    public OrderResponse createOrder(Long customerId, CreateOrderRequest request) {

    }

    public OrderResponse getOrders(Long userId, UUID storeId, OrderStatus status, Integer page, Integer size, String sort) {

    }

    public OrderResponse getOrderById(Long userId, UUID orderId) {

    }

    public OrderResponse updateOrderRequest(Long userId, UUID orderId, String requestText) {

    }

    public OrderResponse updateOrderStatus(Long userId, UUID orderId, OrderStatus status) {

    }

    public OrderResponse cancelOrder(Long userId, UUID orderId){

    }

    public OrderResponse deleteOrder(Long masterId, UUID orderId) {
        // only master
    }



}

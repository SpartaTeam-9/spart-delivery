package com.sparta.spartadelivery.order.presentation.controller;

import com.sparta.spartadelivery.global.infrastructure.config.security.UserPrincipal;
import com.sparta.spartadelivery.global.presentation.dto.ApiResponse;
import com.sparta.spartadelivery.order.application.OrderService;
import com.sparta.spartadelivery.order.presentation.dto.request.OrderCreateRequest;
import com.sparta.spartadelivery.order.presentation.dto.request.UpdateOrderRequest;
import com.sparta.spartadelivery.order.presentation.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody OrderCreateRequest request
            ) {
        OrderResponse response = orderService.createOrder(userPrincipal.getId(), request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "CREATED", response));

    }

    @PutMapping("/{orderId}")
    public ResponseEntity<ApiResponse<?>> updateOrderReqeust(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID orderId,
            @RequestBody UpdateOrderRequest request
            ) {

        orderService.updateOrderRequest(userPrincipal.getId(), orderId, request.message());

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "SUCCESS", null));

    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<?>> cancelOrder(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID orderId

    ) {

        orderService.cancelOrder(userPrincipal.getId(), orderId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK.value(), "SUCCESS", null));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<?>> deleteOrder (
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID orderId
    ) {
        orderService.deleteOrder(userPrincipal.getId(), orderId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(HttpStatus.NO_CONTENT.value(), "DELETED", null));
    }

}

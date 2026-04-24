package com.sparta.spartadelivery.order.presentation.dto.response.OrderSearch;

import com.sparta.spartadelivery.order.domain.entity.OrderStatus;

import java.util.UUID;

public record OwnerOrderResponse(
        UUID orderId,
        Long customerId,
        UUID addressId,
        String address,
        OrderStatus orderStatus,
        String request
) {
}

package com.sparta.spartadelivery.order.presentation.dto.response.OrderSearch;

import com.sparta.spartadelivery.order.domain.entity.OrderStatus;

import java.util.UUID;

public record CustomerOrderResponse(
        UUID orderId,
        UUID storeId,
        String storeName,
        UUID addressId,
        OrderStatus orderStatus,
        String request

) {
}

package com.sparta.spartadelivery.order.presentation.dto.response.OrderSearch;

import com.sparta.spartadelivery.order.domain.entity.OrderStatus;

import java.util.UUID;

public record MasterOrderResponse (
        UUID orderId,
        Long customerId,
        UUID storeId,
        UUID addressId,
        OrderStatus orderStatus
){
}

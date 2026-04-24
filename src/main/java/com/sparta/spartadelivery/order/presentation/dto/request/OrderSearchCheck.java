package com.sparta.spartadelivery.order.presentation.dto.request;

import com.sparta.spartadelivery.order.domain.entity.OrderStatus;

import java.util.UUID;

public record OrderSearchCheck(
        UUID storeId,
        OrderStatus orderStatus
) {
}

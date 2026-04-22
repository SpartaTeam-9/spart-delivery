package com.sparta.spartadelivery.order.presentation.dto.request;

import com.sparta.spartadelivery.order.domain.entity.OrderStatus;
import com.sparta.spartadelivery.user.domain.entity.Role;

import java.util.UUID;


public record SearchOrdersVO (
        Long requesterId,
        Role requesterRole,
        UUID storeId,
        OrderStatus status
) {

}

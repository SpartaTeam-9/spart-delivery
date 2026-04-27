package com.sparta.spartadelivery.order.domain.event;

import java.util.UUID;

public record OrderCreatedEvent (
        Long userId,
        UUID orderId,
        Integer amount
) {
}
